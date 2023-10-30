package cz.libors.particlelife

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*

class Button(
    private val name: String,
    private val tooltip: String = "",
    private val onClick: () -> Unit = {}
) {

    fun attach(table: VisTable): VisTextButton {
        val button = VisTextButton(name)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                onClick()
            }
        })
        if (tooltip.isNotBlank()) {
            button.addListener (TextTooltip(tooltip, VisUI.getSkin()))
        }
        table.add(button)
        return button
    }
}

class SelectBox(
    private val name: String,
    private val options: List<String>,
    private val defaultValue: String,
    private val tooltip: String = "",
    private val onUpdate: (String) -> Unit = { }
) {
    fun attach(table: VisTable) {
        val box = VisSelectBox<String>()
        box.setItems(*options.toTypedArray())
        box.selected = defaultValue
        box.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                onUpdate(box.selected)
            }
        })
        val label = VisLabel(name)
        if (tooltip.isNotBlank()) {
            label.addListener (TextTooltip(tooltip, VisUI.getSkin()))
        }
        table.add(label)
        table.add(box).colspan(2)
    }
}

class Check(
    private val name: String,
    private val default: Boolean,
    private val tooltip: String = "",
    private val onUpdate: (Boolean) -> Unit = { }
) {
    fun attach(table: VisTable): Cell<VisCheckBox> {
        val check = VisCheckBox(name)
        check.isChecked = default
        check.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                onUpdate(check.isChecked)
            }
        })
        if (tooltip.isNotBlank()) {
            check.addListener (TextTooltip(tooltip, VisUI.getSkin()))
        }
        return table.add(check)
    }
}

class Slider(
    private val name: String,
    private val min: Number,
    private val max: Number,
    private val step: Number,
    private val initValue: Number,
    private val decimals: Int,
    private val manualApply: Boolean = false,
    private val tooltip: String = "",
    private val onUpdate: (Float) -> Unit = { }
) {

    fun attach(table: VisTable) {
        val sliderValue = VisLabel()
        val slider = VisSlider(min.toFloat(), max.toFloat(), step.toFloat(), false)
        slider.value = initValue.toFloat()
        sliderValue.setText("%.${decimals}f".format(slider.value))
        val label = VisLabel(name)
        if (tooltip.isNotBlank()) {
            label.addListener (TextTooltip(tooltip, VisUI.getSkin()))
        }
        table.add(label)
        table.add(slider)
        table.add(sliderValue).minWidth(50f)
        if (manualApply) {
            val button = VisTextButton("apply")
            button.isVisible = false
            button.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    onUpdate(slider.value)
                    button.isVisible = false
                }
            })
            slider.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    sliderValue.setText("%.${decimals}f".format(slider.value))
                    button.isVisible = true
                }
            })
            table.add(button)
        } else {
            slider.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    sliderValue.setText("%.${decimals}f".format(slider.value))
                    onUpdate(slider.value)
                }
            })
        }
        table.row()
    }

}