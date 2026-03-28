package com.simats.e_bookmotivation

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import com.simats.e_bookmotivation.network.RetrofitClient
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.navigation.NavOptionsBuilder
import androidx.compose.ui.platform.LocalContext
import com.simats.e_bookmotivation.ui.screens.AudioModeScreen
import com.simats.e_bookmotivation.ui.screens.LoginScreen
import com.simats.e_bookmotivation.ui.screens.OnboardingScreen
import com.simats.e_bookmotivation.ui.screens.PersonalizationScreen
import com.simats.e_bookmotivation.ui.screens.ResetScreen
import com.simats.e_bookmotivation.ui.screens.SignupScreen
import com.simats.e_bookmotivation.ui.screens.SplashScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.AICoachScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.ReflectionsScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.ChallengesScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.HelpAndSupportScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.DashboardScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.DailyBoostScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.LibraryScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.LibraryStatsScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.ProfileScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.ProgressScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.BooksCompletedScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.GoalDetailsScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.PersonalDetailsScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.ReadingAnalyticsScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.SavedQuotesScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.PasswordResetFaqScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.OfflineReadingFaqScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.AICoachFaqScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.ContactSupportScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.ReportBugScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.SuggestFeatureScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.SettingsScreen
import com.simats.e_bookmotivation.ui.screens.dashboard.ChangePasswordScreen
import com.simats.e_bookmotivation.ui.screens.GoalScreen
import com.simats.e_bookmotivation.ui.screens.BadgesScreen
import com.simats.e_bookmotivation.ui.screens.JournalScreen
import com.simats.e_bookmotivation.ui.screens.EditGoalScreen
import com.simats.e_bookmotivation.ui.screens.ReaderScreen
import com.simats.e_bookmotivation.ui.screens.BookDetailsScreen
import com.simats.e_bookmotivation.ui.theme.EBOOKMOTIVATIONTheme
import com.simats.e_bookmotivation.ui.theme.LocalReadingPreferences
import com.simats.e_bookmotivation.ui.theme.ReadingPreferences
import com.simats.e_bookmotivation.ui.screens.AdminLoginScreen
import com.simats.e_bookmotivation.ui.screens.AdminSignupScreen
import com.simats.e_bookmotivation.ui.screens.AdminDashboardScreen
import com.simats.e_bookmotivation.ui.screens.AdminChaptersScreen
import com.simats.e_bookmotivation.ui.screens.JournalDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load auth token on startup
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)
        RetrofitClient.setToken(token)

        enableEdgeToEdge()
        setContent {
            val readingPrefState = remember { mutableStateOf(ReadingPreferences()) }
            
            EBOOKMOTIVATIONTheme(
                themeFlavor = readingPrefState.value.theme,
                fontSize = readingPrefState.value.fontSize
            ) {
                // A surface container using the 'background' color from the theme
                CompositionLocalProvider(LocalReadingPreferences provides readingPrefState) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    val navAction: (String) -> Unit = { route ->
        val bottomNavRoutes = listOf("Home", "Library", "AI Coach", "Progress", "Profile")
        if (route in bottomNavRoutes) {
            navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo("Home") {
                    saveState = true
                    inclusive = false
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        } else {
            // Standard navigation for other screens (e.g. details, settings)
            navController.navigate(route)
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            val context = LocalContext.current
            LoginScreen(
                onLoginSuccess = {
                    val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                    val isStaff = prefs.getBoolean("is_staff", false)
                    if (isStaff) {
                        navController.navigate("AdminDashboard") {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate("Home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate("signup")
                },
                onNavigateToReset = {
                    navController.navigate("reset_password")
                },
                onNavigateToAdminLogin = {
                    navController.navigate("admin_login")
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate("personalization") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToAdminSignup = {
                    navController.navigate("admin_signup")
                }
            )
        }
        composable("admin_login") {
            val context = LocalContext.current
            AdminLoginScreen(
                onLoginSuccess = {
                    val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("is_staff", true).apply()
                    navController.navigate("AdminDashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAdminSignup = {
                    navController.navigate("admin_signup")
                }
            )
        }
        composable("admin_signup") {
            AdminSignupScreen(
                onSignupSuccess = {
                    navController.navigate("AdminDashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("personalization") {
            PersonalizationScreen(
                onNavigateToHome = {
                    navController.navigate("Home") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("Home") {
            DashboardScreen(onNavigate = navAction)
        }
        composable("reset_password") {
            ResetScreen(
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = { 
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("Goal") { GoalScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("Badges") { BadgesScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("Journal") { JournalScreen(onNavigate = navAction, onNavigateBack = { navController.popBackStack() }) }
        composable(
            route = "Reader/{bookId}/{title}/{author}",
            arguments = listOf(
                androidx.navigation.navArgument("bookId") { type = androidx.navigation.NavType.IntType },
                androidx.navigation.navArgument("title") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("author") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 1
            val title = backStackEntry.arguments?.getString("title") ?: "Atomic Habits"
            val author = backStackEntry.arguments?.getString("author") ?: "James Clear"
            ReaderScreen(
                bookId = bookId,
                title = title,
                author = author,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "BookDetails/{bookId}/{title}/{author}",
            arguments = listOf(
                androidx.navigation.navArgument("bookId") { type = androidx.navigation.NavType.IntType },
                androidx.navigation.navArgument("title") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("author") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 1
            val title = backStackEntry.arguments?.getString("title") ?: "Unknown Book"
            val author = backStackEntry.arguments?.getString("author") ?: "Unknown Author"
            BookDetailsScreen(
                bookId = bookId,
                title = title,
                author = author,
                onNavigateBack = { navController.popBackStack() },
                onStartReading = { 
                    navController.navigate("Reader/$bookId/${android.net.Uri.encode(title)}/${android.net.Uri.encode(author)}") 
                }
            ) 
        }
        composable(
            route = "AudioMode/{bookId}/{title}/{author}",
            arguments = listOf(
                androidx.navigation.navArgument("bookId") { type = androidx.navigation.NavType.IntType },
                androidx.navigation.navArgument("title") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("author") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 1
            val title = backStackEntry.arguments?.getString("title") ?: "Unknown Book"
            val author = backStackEntry.arguments?.getString("author") ?: "Unknown Author"
            AudioModeScreen(
                bookId = bookId,
                title = title,
                author = author,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("Library") { LibraryScreen(onNavigate = navAction) }
        composable("LibraryStats") {
            LibraryStatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("Profile") {
            val context = LocalContext.current
            ProfileScreen(
                onNavigate = navAction,
                onLogout = {
                    com.simats.e_bookmotivation.util.SessionManager.clearSession(context)
                    
                    // Navigate to splash and clear entire backstack
                    navController.navigate("splash") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("Settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPersonalDetails = { navController.navigate("PersonalDetails") },
                onNavigateToChangePassword = { navController.navigate("ChangePassword") },
                onNavigateToReadingPreferences = { navController.navigate("ReadingPreferences") },
                onNavigateToNotificationSettings = { navController.navigate("NotificationSettings") },
                onNavigateToPrivacyPolicy = { navController.navigate("PrivacyPolicy") },
                onNavigateToHelpAndSupport = { navController.navigate("HelpAndSupport") }
            )
        }
        composable("ChangePassword") {
            ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("EditProfile") {
            com.simats.e_bookmotivation.ui.screens.dashboard.EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveProfile = { _, _, _ ->
                    // Logic to handle saving the profile would go here
                    navController.popBackStack()
                }
            )
        }
        composable("ReadingPreferences") {
            com.simats.e_bookmotivation.ui.screens.dashboard.ReadingPreferencesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("GrowthStats") {
            com.simats.e_bookmotivation.ui.screens.dashboard.GrowthStatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("Notifications") {
            com.simats.e_bookmotivation.ui.screens.dashboard.NotificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("NotificationSettings") {
            com.simats.e_bookmotivation.ui.screens.dashboard.NotificationSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("Language") {
            com.simats.e_bookmotivation.ui.screens.dashboard.LanguageScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("Subscription") {
            com.simats.e_bookmotivation.ui.screens.dashboard.SubscriptionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("PrivacyPolicy") {
            com.simats.e_bookmotivation.ui.screens.dashboard.PrivacyPolicyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("HelpAndSupport") {
            HelpAndSupportScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPasswordResetFaq = { navController.navigate("PasswordResetFaq") },
                onNavigateToOfflineReadingFaq = { navController.navigate("OfflineReadingFaq") },
                onNavigateToAICoachFaq = { navController.navigate("AICoachFaq") },
                onNavigateToContactSupport = { navController.navigate("ContactSupport") },
                onNavigateToReportBug = { navController.navigate("ReportBug") },
                onNavigateToSuggestFeature = { navController.navigate("SuggestFeature") }
            )
        }
        composable("PasswordResetFaq") {
            PasswordResetFaqScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("OfflineReadingFaq") {
            OfflineReadingFaqScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("AICoachFaq") {
            AICoachFaqScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("ContactSupport") {
            ContactSupportScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("ReportBug") {
            ReportBugScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("SuggestFeature") {
            SuggestFeatureScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("DailyBoost") {
            DailyBoostScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("AI Coach") { AICoachScreen(onNavigate = navAction) }
        composable("Progress") { ProgressScreen(onNavigate = navAction) }
        composable("GoalDetails") { 
            GoalDetailsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigate = navAction
            ) 
        }
        composable("BooksCompleted") {
            BooksCompletedScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("Reflections") {
            ReflectionsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("Challenges") {
            ChallengesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("PersonalDetails") {
            PersonalDetailsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("ReadingAnalytics") {
            ReadingAnalyticsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("SavedQuotes") {
            SavedQuotesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = "JournalDetail/{entryId}",
            arguments = listOf(
                androidx.navigation.navArgument("entryId") { type = androidx.navigation.NavType.IntType }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getInt("entryId") ?: 0
            JournalDetailScreen(
                entryId = entryId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        // Admin Screens
        composable("AdminDashboard") {
            val context = LocalContext.current
            AdminDashboardScreen(
                onNavigateToChapters = { bookId ->
                    navController.navigate("AdminChapters/$bookId")
                },
                onLogout = {
                    com.simats.e_bookmotivation.util.SessionManager.clearSession(context)
                    navController.navigate("splash") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "AdminChapters/{bookId}",
            arguments = listOf(
                androidx.navigation.navArgument("bookId") { type = androidx.navigation.NavType.IntType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
            AdminChaptersScreen(
                bookId = bookId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}