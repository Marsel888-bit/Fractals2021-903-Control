package ru.smak.ui.painting

import ru.smak.ui.GraphicsPanel
import java.awt.Component
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel

class KeyFramesPanel : JPanel() {

    // Массив из "мини-панелей" с их отступами
    private val KFwithGaps : ArrayList<Pair<GraphicsPanel,Component>> = arrayListOf()
    // Массив с изображениями ключевых кадров на "мини-панелях" и объектами плоскостей
    val keyFrames : ArrayList<Pair<BufferedImage, CartesianPlane>> = arrayListOf<Pair<BufferedImage, CartesianPlane>>()
    // Размер каждой "мини-панели" с ключевым кадром
    val KFsize = Dimension(300, 100)
    // Размер отступа между "мини-панелями"
    private val gap = 5
    init {
        // В качестве менеджера раскладки берем BoxLayout с вертикальным расположением компонентов
        layout = BoxLayout(this, BoxLayout.Y_AXIS).apply {
        }

        // Обработчик события по нажатию на текущую панель
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                e?.apply {
                    if (button == MouseEvent.BUTTON1) {
                        // Получаем "мини-панель", на которой кликнул пользователь
                        val delPanel = KFwithGaps.firstOrNull {
                            (x >= it.first.x && x <= it.first.width && y >= it.first.y && y <= it.first.y + it.first.height)
                        }
                        if (delPanel != null)
                        // Удаялем эту  "мини-панель" с текущей панели
                            deleteKeyFramePanel(KFwithGaps.indexOf(delPanel))
                    }
                }
            }
        })
    }


    fun addKeyFramePanel(img: BufferedImage, plane : CartesianPlane) {
        // Добавляем в массив изображений с соответствующими объектами плоскостей полученные данные
        val _plane = CartesianPlane(plane.xMin, plane.xMax, plane.yMin, plane.yMax)
        keyFrames.add(Pair(img, _plane))
        // Создаем панель, которая содержит панель ключевых кадров
        val keyFramePanel = GraphicsPanel(ImagePainter(img, KFsize)).apply {
            preferredSize = KFsize
            maximumSize = preferredSize
            setSize(KFsize)
        }
        // Добавляем на текущую панель с ключевыми кадрами новую "мини-панель" с ключевым кадром
        add(keyFramePanel)
        // Cоздаем вертикальный отступ
        val vertGap = Box.createVerticalStrut(gap)
        // Добавляем этот оступ на текущую панель, после добавленного ключевого кадра
        add(vertGap)
        // Добавляем "мини-панель" и отступ в массив KFwithGaps
        KFwithGaps.add(Pair(keyFramePanel, vertGap))
        // Обновляем текущую панель с ключевыми кадрами
        revalidate()
    }

    fun deleteKeyFramePanel(index: Int) {
        // Получаем по индексу панель, которую необходимо удалить
        val currFramePanel = KFwithGaps.get(index).first
        // Получаем отступ соответствующий панели currFramePanel
        val vGap = KFwithGaps.get(index).second
        // Удаляем с текущей панели отступ и "мини-панель"
        remove(vGap)
        remove(currFramePanel)
        KFwithGaps.removeAt(index)
        keyFrames.removeAt(index)
        revalidate()
        repaint()
    }

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
