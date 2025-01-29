package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.sftp.server.SftpSubsystemFactory
import java.nio.file.Files
import java.nio.file.Path
import java.security.PublicKey
import kotlin.io.path.readBytes

class LocalSftpServer(
    val hostKeyPath: Path,
    val authorizedKeysPath: Path,
    val sftpRootDir: Path,
) {
    private val sshServer: SshServer = configureServer()

    fun configureServer(): SshServer {
        if (!Files.exists(sftpRootDir)) {
            throw LocalSftpServerException("sftpRootDir does not exist: $sftpRootDir")
        }

        if (!Files.exists(authorizedKeysPath)) {
            throw LocalSftpServerException("authorized keys path does not exist: $authorizedKeysPath")
        }

        val sshServer = SshServer.setUpDefaultServer().apply {
            port = port
            host = "127.0.0.1"
            keyPairProvider = SimpleGeneratorHostKeyProvider(hostKeyPath)
            publickeyAuthenticator = AuthorizedKeysAuthenticator(TestSftpConfig.authorizedKeys)
            subsystemFactories = listOf(SftpSubsystemFactory())
            fileSystemFactory = VirtualFileSystemFactory(sftpRootDir)
        }
        return sshServer
    }

    fun start() {
        sshServer.start()
    }

    fun stop() {
        sshServer.stop()
    }

    private fun isKeyAuthorized(key: PublicKey, path: Path): Boolean {
        println("isKeyAuthorized: $key, $path")
        val authorizedKeys = Files.readAllLines(authorizedKeysPath)
        val keyString = key.encoded.joinToString(separator = "") {
            String.format("%02x", it)
        }
        val publicKey = TestSftpConfig.clientPublic.toByteArray(Charsets.UTF_8)
        return true
    }

    fun getPort(): Int = sshServer.port

    companion object {
        fun default() = LocalSftpServer(
            hostKeyPath = TestSftpConfig.serverPrivate,
            authorizedKeysPath = TestSftpConfig.authorizedKeys,
            sftpRootDir = TestSftpConfig.sftpFilePath,
        )
    }

    class LocalSftpServerException(message: String) : RuntimeException(message)
}