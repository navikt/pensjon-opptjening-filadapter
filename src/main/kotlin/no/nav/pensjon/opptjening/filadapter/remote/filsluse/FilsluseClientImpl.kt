package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.readBytes

class FilsluseClientImpl(
    val host: String,
    val port: Int,
    val username: String,
    val privateKeyPath: Path,
) : FilsluseClient {

    override fun scanForFiles(remoteDir: String): List<RemoteFilInfo> {
        try {
            val jsch = JSch()
            val sftpChannel = connectAndOpenSftpChannel(jsch)
            val files = sftpChannel.ls(remoteDir)
            return files.map {
                println("$it ${it.attrs.size} ${it.attrs.mTime} :: ${it.attrs}")
                RemoteFilInfo(it.filename)
            }
        } catch (t: Throwable) {
            throw mapException(t)
        }
    }

    private fun connectAndOpenSftpChannel(jsch: JSch): ChannelSftp {
        println("privateKeyPath: $privateKeyPath")
        val privateKey = privateKeyPath.readBytes()
        jsch.addIdentity("pensjon-opptjening-filadapter", privateKey, null, null)
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