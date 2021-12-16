package ru.smak.ui.painting

import org.intellij.lang.annotations.JdkConstants
import ru.smak.ui.GraphicsPanel
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.*
import javax.swing.text.BoxView

class KeyFramesPanel : JPanel() {

    private val KFwithGaps : ArrayList<Pair<GraphicsPanel,Component>> = arrayListOf()


    val keyFrames : ArrayList<GraphicsPanel>
        get() {
            val _keyFrames = arrayListOf<GraphicsPanel>()
            KFwithGaps.forEach {
                _keyFrames.add(it.first)
            }
            return _keyFrames
        }

    val KFsize = Dimension(300, 100)
    private val gap = 5
    private val borderSz = 1

    init {
        //layout = null
        layout = BoxLayout(this, BoxLayout.Y_AXIS).apply {
        }

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                e?.apply {
                    if (button == MouseEvent.BUTTON1) {

                        val delKF = KFwithGaps.firstOrNull {
                            (x >= it.first.x && x <= it.first.width && y >= it.first.y && y <= it.first.y + it.first.height)
                        }
                        if (delKF != null)
                            deleteKeyFrame(KFwithGaps.indexOf(delKF))
                    }
                }
            }
        })
    }


    fun addKeyFrame(img: BufferedImage) {
        val keyFrame = GraphicsPanel(ImagePainter(img, KFsize)).apply {
            preferredSize = KFsize
            maximumSize = preferredSize
            setSize(KFsize)
        }
        //calcFramePosition(keyFrame)
        add(keyFrame)
        val vertGap = Box.createVerticalStrut(gap)
        add(vertGap)
        KFwithGaps.add(Pair(keyFrame, vertGap))
        revalidate()
    }

    fun deleteKeyFrame(index: Int) {
        val currFrame = KFwithGaps.get(index).first
        val vGap = KFwithGaps.get(index).second
        remove(vGap)
        remove(currFrame)
        KFwithGaps.removeAt(index)
        //reCalcFramesPositions()
        revalidate()
        repaint()
    }

    /*fun addBuffImg(img: BufferedImage?) {
        if (img != null){
            buffImgList.add(img)
        }
    }*/

    /*fun delBuffImg(img: BufferedImage) {*/

    // Производим правильное размещение ключевого кадра, при его добавлении
   /* private fun calcFramePosition(keyFrame: GraphicsPanel) {
        val indexOfFrame = if (keyFrames.isEmpty()) 0 else keyFrames.lastIndex + 1
        with(keyFrame) {
          //setLocation(borderSz, indexOfFrame * (size.height + gap))
            setBounds(0, indexOfFrame * (size.height + gap), size.width, size.height)
        }
    }*/

    /*private fun reCalcFramesPositions() {
        keyFrames.forEachIndexed { i, kf ->
            kf.setLocation(borderSz, i * (kf.size.height + gap))
        }
        revalidate()
    }*/


}
