package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.sftp.server.SftpSubsystemFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.PublicKey

class LocalSftpServer(
    val port: Int,
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
            keyPairProvider = SimpleGeneratorHostKeyProvider(hostKeyPath)
            publickeyAuthenticator = PublickeyAuthenticator { username, key, session ->
                isKeyAuthorized(key, authorizedKeysPath)
            }
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
        val authorizedKeys = Files.readAllLines(authorizedKeysPath)
        val keyString = key.encoded.joinToString(separator = "") {
            String.format("%02x", it)
        }
        return false
    }

    companion object {
        fun default() = LocalSftpServer(
            port = 9999,
            hostKeyPath = Paths.get(Companion::class.java.getResource("/test_id_rsa")!!.toURI()),
            authorizedKeysPath = Paths.get(Companion::class.java.getResource("/test_id_client_rsa.pub")!!.toURI()),
            sftpRootDir = Paths.get(Companion::class.java.getResource("/sftp_files")!!.toURI()),
        )
    }

    class LocalSftpServerException(message: String) : RuntimeException(message)
}