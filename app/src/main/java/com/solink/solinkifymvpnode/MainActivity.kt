 package com.solink.solinkifymvpnode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.solink.solinkifymvpnode.ui.theme.SolinkifyMVPnodeTheme
import com.solink.solinkifymvpnode.service.NodeService
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
 import com.solink.solinkifymvpnode.state.NodeState
import android.content.Intent
import androidx.core.content.ContextCompat
import com.solink.solinkifymvpnode.service.LiteNodeForegroundService

class MainActivity : ComponentActivity() {
    private val nodeService = NodeService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(
            this,
            LiteNodeForegroundService::class.java
        )

        ContextCompat.startForegroundService(
            this,
            serviceIntent
        )


        enableEdgeToEdge()

        nodeService.startNode()

        setContent {
            SolinkifyMVPnodeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DashboardScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

 @Composable
 fun DashboardScreen(modifier: Modifier = Modifier) {

     Column(
         modifier = modifier
             .fillMaxSize()
             .padding(16.dp),

         verticalArrangement = Arrangement.spacedBy(16.dp)
     ) {

         Text(
             text = "Solinkify Lite Node",
             style = MaterialTheme.typography.headlineMedium
         )

         Card(
             modifier = Modifier.fillMaxWidth(),
             colors = CardDefaults.cardColors()
         ) {

             Column(
                 modifier = Modifier.padding(16.dp)
             ) {

                 Text("Status")

                 Text(
                     if (NodeState.connected)
                         "🟢 Connected"
                     else
                         "🔴 Disconnected"
                 )

             }

         }

         Card(
             modifier = Modifier.fillMaxWidth(),
             colors = CardDefaults.cardColors()
         ) {

             Column(
                 modifier = Modifier.padding(16.dp)
             ) {

                 Text("Jobs Relayed")
                 Text(NodeState.jobsRelayed.toString())

             }

         }

         Card(
             modifier = Modifier.fillMaxWidth(),
             colors = CardDefaults.cardColors()
         ) {

             Column(
                 modifier = Modifier.padding(16.dp)
             ) {

                 Text("Last Log")
                 Text(NodeState.lastLog)

             }

         }

     }

 }

 @Preview(showBackground = true)
 @Composable
 fun DashboardPreview() {
     SolinkifyMVPnodeTheme {
         DashboardScreen()
     }
 }



