package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import java.io.InputStream
import java.nio.charset.StandardCharsets

class FilsluseKlientImpl(
    val host: String,
    val port: Int,
    val username: String,
    val privateKey: String,
    val publicKey: String,
    val privateKeyPassword: String,
) : FilsluseKlient {

    companion object {
        private val log = NAVLog(FilsluseKlientImpl::class)
    }

    override fun scanForFiles(remoteDir: String): List<RemoteFilInfo> {
        try {
            val jsch = JSch()
            val sftpChannel = connectAndOpenSftpChannel(jsch)
            val files = sftpChannel.ls(remoteDir)
            return files.map {
                RemoteFilInfo(
                    name = it.filename,
                    size = it.attrs.size,
                )
            }
        } catch (t: Throwable) {
            log.secure.info("Fikk feil ved scanning etter filer", t)
            throw mapException(t)
        }
    }

    override fun scanForFil(remoteDir: String, filnavn: String): RemoteFilInfo? {
        val jsch = JSch()
        val sftpChannel = connectAndOpenSftpChannel(jsch)
        sftpChannel.cd(remoteDir)
        return try {
            val lsEntries = sftpChannel.ls(filnavn)
            log.secure.info("Listet én fil: $filnavn -> $lsEntries")
            if (lsEntries.size > 1) throw SftpClientException.MoreThanOneEntry("$filnavn -> $lsEntries")
            lsEntries[0].let {
                RemoteFilInfo(
                    name = it.filename,
                    size = it.attrs.size,
                )
            }
        } catch (e: Throwable) {
            println("${e.javaClass} msg=${e.message}")
            null
        }
    }

    private fun connectAndOpenSftpChannel(jsch: JSch): ChannelSftp {
        val privateKey = privateKey.toByteArray(StandardCharsets.UTF_8)
        val publicKey = publicKey.toByteArray(StandardCharsets.UTF_8)
        jsch.addIdentity(
            "pensjon-opptjening-filadapter",
            privateKey,
            publicKey,
            privateKeyPassword.toByteArray(StandardCharsets.UTF_8)
        )
        log.open.info("Connecting to $host:$port")
        log.secure.info("Connecting to $username@$host:$port")
        val session = jsch.getSession(username, host, port)
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect()
        log.open.info("SFTP Client: Connected to host")
        val sftpChannel = session.openChannel("sftp") as ChannelSftp
        sftpChannel.connect()
        return sftpChannel
    }

    override fun downloadFile(remoteDir: String, fileName: String): InputStream {
        try {
            val jsch = JSch()
            val sftpChannel = connectAndOpenSftpChannel(jsch)
            sftpChannel.cd(remoteDir)
            return sftpChannel.get(fileName)
        } catch (t: Throwable) {
            log.secure.info("Fikk feil ved nedlasting av fil: $fileName", t)
            throw mapException(t)
        }
    }

    private fun mapException(e: Throwable): Throwable {
        return if ("No such file or directory" == e.message) {
            SftpClientException.NoSuchFileOrDirectory(e)
        } else {
            SftpClientException.Unmapped(e.message, e)
        }
    }

    sealed class SftpClientException(msg: String?, cause: Throwable?) : RuntimeException(msg, cause) {
        class NoSuchFileOrDirectory(throwable: Throwable?) : SftpClientException("", throwable)
        class Unmapped(msg: String?, throwable: Throwable?) : SftpClientException(msg, throwable)
        class MoreThanOneEntry(msg: String) : SftpClientException(msg, null)
    }
}