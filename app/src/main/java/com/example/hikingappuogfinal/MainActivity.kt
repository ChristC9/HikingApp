package com.example.hikingappuogfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hikingappuogfinal.data.db.AppDatabase
import com.example.hikingappuogfinal.data.repo.HikeRepository
import com.example.hikingappuogfinal.ui.HikeDetailViewModel
import com.example.hikingappuogfinal.ui.HikeFormViewModel
import com.example.hikingappuogfinal.ui.HikeListViewModel
import com.example.hikingappuogfinal.ui.VmFactory
import com.example.hikingappuogfinal.ui.screens.HikeDetailScreen
import com.example.hikingappuogfinal.ui.screens.HikeFormScreen
// If your form screen is named HikeFormScreenCompact, import that instead:
// import com.example.hikingappuogfinal.ui.screens.HikeFormScreenCompact
import com.example.hikingappuogfinal.ui.screens.HikeListScreen
import com.example.hikingappuogfinal.ui.theme.MHikeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.get(this)
        val repo = HikeRepository(db.hikeDao(), db.observationDao())

        setContent {
            MHikeTheme {
                val nav = rememberNavController()
                val factory = VmFactory(repo)

                NavHost(navController = nav, startDestination = "list") {
                    composable("list") {
                        val vm: HikeListViewModel = viewModel(factory = factory)
                        HikeListScreen(
                            vm = vm,
                            onAdd = { nav.navigate("form") },
                            onOpen = { id -> nav.navigate("detail/$id") },
                            onEdit = { id -> nav.navigate("form?editId=$id") }
                        )
                    }

                    composable(
                        route = "form?editId={editId}",
                        arguments = listOf(
                            navArgument("editId") {
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                    ) {
                        val vm: HikeFormViewModel = viewModel(factory = factory)
                        HikeFormScreen( // or HikeFormScreenCompact if that’s what you kept
                            vm = vm,
                            editId = it.arguments?.getString("editId")?.toLongOrNull(),
                            onDone = { id ->
                                nav.navigate("detail/$id") {
                                    popUpTo("list") { inclusive = false }
                                }
                            },
                            onCancel = { nav.popBackStack() }
                        )
                    }

                    composable(
                        route = "detail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.LongType })
                    ) {
                        val id = it.arguments!!.getLong("id")
                        val vm: HikeDetailViewModel = viewModel(factory = factory)
                        vm.start(id)
                        HikeDetailScreen(id = id, vm = vm, onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
