package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import java.nio.file.Paths

object TestSftpConfig {
    val clientPrivate = Paths.get(this::class.java.getResource("/test_id_client_rsa")!!.toURI())
    val clientPublic = Paths.get(this::class.java.getResource("/test_id_client_rsa.pub")!!.toURI())
    val serverPrivate = Paths.get(this::class.java.getResource("/test_id_rsa")!!.toURI())
    val serverPublic = Paths.get(this::class.java.getResource("/test_id_rsa.pub")!!.toURI())
    val authorizedKeys = Paths.get(this::class.java.getResource("/test_authorized_keys")!!.toURI())

    val sftpFilePath = Paths.get(this::class.java.getResource("/sftp_files")!!.toURI())
}