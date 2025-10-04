package com.issuetrax.app.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.issuetrax.app.presentation.ui.common.theme.IssuetraxTheme
import com.issuetrax.app.presentation.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private var oauthCallback: ((String) -> Unit)? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle OAuth callback
        handleOAuthCallback(intent)
        
        setContent {
            IssuetraxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        onOAuthCallbackRegistered = { callback ->
                            oauthCallback = callback
                        }
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthCallback(intent)
    }
    
    private fun handleOAuthCallback(intent: Intent?) {
        val uri: Uri? = intent?.data
        
        if (uri != null && uri.scheme == "issuetrax" && uri.host == "oauth") {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                oauthCallback?.invoke(code)
            }
        }
    }
}