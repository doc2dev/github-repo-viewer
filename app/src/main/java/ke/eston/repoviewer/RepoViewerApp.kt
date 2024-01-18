package ke.eston.repoviewer

import android.app.Application
import ke.eston.repoviewer.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class RepoViewerApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        setupTimber()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@RepoViewerApp)
            modules(appModule)
        }
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}