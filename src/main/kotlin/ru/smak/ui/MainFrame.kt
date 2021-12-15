package ru.smak.ui

import ru.smak.math.fractals.Mandelbrot
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.SelectablePanel
import ru.smak.ui.painting.fractals.FractalPainter
import ru.smak.ui.painting.fractals.colorizers
import java.awt.Color
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.GroupLayout
import javax.swing.JButton
import javax.swing.JFrame
import kotlin.random.Random

class MainFrame : JFrame() {
    val animFrame : AnimationFrame
    val openAnimFrame : JButton
    val fractalPanel: SelectablePanel

    init{
        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(600, 400)

        openAnimFrame = JButton().apply {
            text = "Открыть экскурсию"
        }


        val painter = FractalPainter(
            Mandelbrot(),
            CartesianPlane(-2.0, 1.0, -1.0, 1.0),
            colorizers[Random.nextInt(colorizers.size)])

        fractalPanel = SelectablePanel(painter).apply {
            background = Color.WHITE
            addSelectListener{
                with (painter.plane){
                    val xMin = xScr2Crt(it.x)
                    val yMin = yScr2Crt(it.y)
                    val xMax = xScr2Crt(it.x + it.width)
                    val yMax = yScr2Crt(it.y + it.height)
                    xSegment = Pair(xMin, xMax)
                    ySegment = Pair(yMin, yMax)
                }
                repaint()
            }
        }
        animFrame = AnimationFrame(fractalPanel)
        openAnimFrame.addMouseListener( object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                animFrame.isVisible = true
            }
        })


        layout = GroupLayout(contentPane).apply {
            setHorizontalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addComponent(fractalPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                    .addComponent(openAnimFrame, 100, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(4)
            )
            setVerticalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addComponent(fractalPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                    .addComponent(openAnimFrame, 30, 30, 30)
                    .addGap(4)
            )
        }
    }

    //fun onSelectArea(r: Rectangle)
}