package id.dpradana.themoviedb.core.network.config

object NetworkConfig {
    init {
        System.loadLibrary("network")
    }

    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    
    val TMDB_API_KEY: String
        get() = getApiKey()

    private external fun getApiKey(): String
}
