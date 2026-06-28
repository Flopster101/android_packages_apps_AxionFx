@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.android.axion.axionfx.ui.screens

import com.android.axion.axionfx.ui.AxionFxViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.android.axion.axionfx.R
import com.android.axion.axionfx.domain.EffectKeys
import com.android.axion.axionfx.domain.EffectDefaults
import com.android.axion.axionfx.ui.components.EffectSlider
import com.android.axion.compose.preferences.PreferenceGroup
import com.android.axion.compose.preferences.SwitchPreference
import com.android.axion.compose.scaffold.AxionScaffold

private const val KEY_TSHAPER_ENABLED = "tshaper_enabled"
private const val KEY_TSHAPER_ATTACK = "tshaper_attack"
private const val KEY_TSHAPER_SUSTAIN = "tshaper_sustain"

@Composable
fun TransientShaperScreen(viewModel: AxionFxViewModel, onBackClick: () -> Unit) {
    BackHandler(onBack = onBackClick)

    var enabled by remember { mutableStateOf(viewModel.loadBoolean(KEY_TSHAPER_ENABLED, false)) }
    var attack by remember { mutableFloatStateOf(viewModel.loadInt(KEY_TSHAPER_ATTACK, 0).toFloat()) }
    var sustain by remember { mutableFloatStateOf(viewModel.loadInt(KEY_TSHAPER_SUSTAIN, 0).toFloat()) }

    AxionScaffold(title = stringResource(R.string.tshaper_screen_title), onBackClick = onBackClick) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            PreferenceGroup(title = stringResource(R.string.tshaper_category)) {
                item {
                    SwitchPreference(
                        title = stringResource(R.string.tshaper_enable_title),
                        summary = stringResource(R.string.tshaper_enable_summary),
                        checked = enabled,
                        onCheckedChange = {
                            enabled = it
                            viewModel.interactor.setTransientShaperEnabled(it)
                        },
                    )
                }
                item {
                    EffectSlider(
                        title = stringResource(R.string.tshaper_attack_title),
                        summary = stringResource(R.string.tshaper_attack_summary),
                        value = attack,
                        valueRange = -100f..100f,
                        unit = "%",
                        enabled = enabled,
                        onValueChange = {
                            attack = it
                            viewModel.interactor.setTransientShaperAttack(it.toInt())
                        },
                    )
                }
                item {
                    EffectSlider(
                        title = stringResource(R.string.tshaper_sustain_title),
                        summary = stringResource(R.string.tshaper_sustain_summary),
                        value = sustain,
                        valueRange = -100f..100f,
                        unit = "%",
                        enabled = enabled,
                        onValueChange = {
                            sustain = it
                            viewModel.interactor.setTransientShaperSustain(it.toInt())
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
