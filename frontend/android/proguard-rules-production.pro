# Remove LogCat Tree anyway
-assumenosideeffects class io.github.droidkaigi.confsched2019.App {
    public *** enableLogCatLogging();
}

# Remove Android Log's methods
-assumenosideeffects class android.util.Log {
    public static *** e(...);
    public static *** d(...);
    public static *** i(...);
    public static *** v(...);
    public static *** w(...);
    public static *** wtf(...);
}

# Remove log methods which CrashlyticsTree doesn't support
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** i(...);
    public static *** v(...);
    public static *** w(...);
    public static *** wtf(...);
}

# Remove our log methods which are lower than ERROR
-assumenosideeffects class io.github.droidkaigi.confsched2019.timber.Timber {
    *** info(...);
    *** warn(...);
    *** debug(...);
    *** verbose(...);
}
