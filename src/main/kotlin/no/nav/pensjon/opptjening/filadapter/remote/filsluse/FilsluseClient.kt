package no.nav.pensjon.opptjening.filadapter.remote.filsluse

interface FilsluseClient {
    fun scanForFiles(): List<RemoteFilInfo>
}