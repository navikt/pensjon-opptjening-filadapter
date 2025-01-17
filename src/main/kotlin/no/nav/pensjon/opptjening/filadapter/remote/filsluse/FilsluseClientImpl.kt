package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import java.nio.file.Path

class FilsluseClientImpl(
    val host: String,
    val port: Int,
    val username: String,
    val privateKeyPath: Path,
) : FilsluseClient {

    override fun scanForFiles(remoteDir: String): List<RemoteFilInfo> {
        val jsch = JSch()
        println("privateKeyPath: $privateKeyPath")
        jsch.addIdentity(privateKeyPath.toString())
        val session = jsch.getSession(username, host, port)
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect()
        println("SFTP Client: Connected to host")
        val sftpChannel = session.openChannel("sftp") as ChannelSftp
        sftpChannel.connect()
        println("SFTP Client: SFPT channel opened")

        val files = sftpChannel.ls(remoteDir)
        return files.map {
            println("$it")
            RemoteFilInfo(it.filename)
        }
    }
}