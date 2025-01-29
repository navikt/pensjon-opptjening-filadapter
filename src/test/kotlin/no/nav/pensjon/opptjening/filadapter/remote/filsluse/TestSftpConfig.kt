package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths

object TestSftpConfig {
    val clientPrivate = readAsString("/test_id_client_rsa")
    val clientPublic = readAsString("/test_id_client_rsa.pub")
    val serverPrivate = toPath("/test_id_rsa")
    val serverPublic = toPath("/test_id_rsa.pub")
    val authorizedKeys = toPath("/test_authorized_keys")

    val sftpFilePath = Paths.get(this::class.java.getResource("/sftp_files")!!.toURI())

    private fun readAsString(classpathPath: String): String {
        return String(this::class.java.getResourceAsStream(classpathPath)!!.readAllBytes(), StandardCharsets.UTF_8)
    }

    private fun toPath(classpathPath: String): Path {
        return Paths.get(this::class.java.getResource(classpathPath)!!.toURI()!!)
    }
}