package ru.smak.ui.painting

import ru.smak.ui.GraphicsPanel
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.Box



class Video {
    private val buffImgList: MutableList<BufferedImage>

    init {
        buffImgList = mutableListOf<BufferedImage>().apply {






        }
    }

    fun addBuffImg(img: BufferedImage) {
        buffImgList.add(img)
    }

    fun delBuffImg(img: BufferedImage) {
        buffImgList.forEach {
            if (img == it){
                buffImgList.remove(it)
            }
        }
        //buffImgList.remove(img)
    }













}