package au.edu.jcu.assessment.utilityapp

import android.app.Application
import au.edu.jcu.assessment.utilityapp.data.AppContainer
import au.edu.jcu.assessment.utilityapp.data.DefaultAppContainer

/** Holds the dependency container for the lifetime of the app process. */
class SoutherlyApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}