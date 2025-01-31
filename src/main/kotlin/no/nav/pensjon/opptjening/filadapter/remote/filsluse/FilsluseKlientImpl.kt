package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import java.io.InputStream
import java.nio.charset.StandardCharsets

class FilsluseKlientImpl(
    val host: String,
    val port: Int,
    val username: String,
    val privateKey: String,
    val privateKeyPassword: String,
) : FilsluseKlient {

    override fun scanForFiles(remoteDir: String): List<RemoteFilInfo> {
        try {
            val jsch = JSch()
            val sftpChannel = connectAndOpenSftpChannel(jsch)
            val files = sftpChannel.ls(remoteDir)
            return files.map {
                RemoteFilInfo(it.filename)
            }
        } catch (t: Throwable) {
            throw mapException(t)
        }
    }

    private fun connectAndOpenSftpChannel(jsch: JSch): ChannelSftp {
        val privateKey = privateKey.toByteArray(StandardCharsets.UTF_8)
        jsch.addIdentity(
            "pensjon-opptjening-filadapter",
            privateKey,
            null,
            privateKeyPassword.toByteArray(StandardCharsets.UTF_8)
        )
        val session = jsch.getSession(username, host, port)
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect()
        println("SFTP Client: Connected to host")
        val sftpChannel = session.openChannel("sftp") as ChannelSftp
        sftpChannel.connect()
        return sftpChannel
    }

    override fun downloadFile(fileName: String): InputStream {
        try {
            val jsch = JSch()
            val sftpChannel = connectAndOpenSftpChannel(jsch)
            return sftpChannel.get(fileName)
        } catch (t: Throwable) {
            throw mapException(t)
        }
    }

    fun mapException(e: Throwable): Throwable {
        return if ("No such file or directory" == e.message) {
            SftpClientException.NoSuchFileOrDirectory(e)
        } else {
            SftpClientException.Unmapped(e.message, e)
        }
    }

    sealed class SftpClientException(msg: String?, cause: Throwable?) : RuntimeException(msg, cause) {
        class NoSuchFileOrDirectory(throwable: Throwable?) : SftpClientException("", throwable)
        class Unmapped(msg: String?, throwable: Throwable?) : SftpClientException(msg, throwable)
    }
}