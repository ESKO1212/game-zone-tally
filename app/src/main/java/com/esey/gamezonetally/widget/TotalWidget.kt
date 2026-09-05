package com.esey.gamezonetally.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.esey.gamezonetally.data.CounterRepository
import com.esey.gamezonetally.data.Group
import com.esey.gamezonetally.data.Groups

/**
 * Same rule as station widgets: each physical widget needs its own class.
 * Glance tracks widget instances by class, not constructor args.
 */
abstract class BaseTotalWidget(private val group: Group) : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = CounterRepository(context)
        provideContent {
            val total by repo.totalFlow(group).collectAsState(initial = 0)
            TotalContent(group = group, total = total)
        }
    }
}

class Ps5TotalWidget : BaseTotalWidget(Groups.PS5)
class XboxTotalWidget : BaseTotalWidget(Groups.XBOX)

/** Refreshes both group total widgets after a data change. */
object TotalWidgetHost {
    suspend fun updateAll(context: Context) {
        Ps5TotalWidget().updateAll(context)
        XboxTotalWidget().updateAll(context)
    }
}

@Composable
private fun TotalContent(group: Group, total: Int) {
    val params = actionParametersOf(GROUP_ID_PARAM to group.id)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "${group.label} Total",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.White)
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = (total * 15).toString(),
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.LightGray)
                )
            )
        }
        Column(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = total.toString(),
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.White)
                )
            )
        }
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(
                text = "reset all",
                modifier = GlanceModifier.clickable(actionRunCallback<ResetGroupAction>(params)),
                style = TextStyle(fontSize = 11.sp, color = ColorProvider(Color.LightGray))
            )
        }
    }
}

class Ps5TotalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = Ps5TotalWidget()
}

class XboxTotalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = XboxTotalWidget()
}
