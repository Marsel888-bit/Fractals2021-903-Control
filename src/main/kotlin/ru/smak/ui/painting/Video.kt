package ru.smak.ui.painting

import java.io.humble.video.*
import java.io.humble.video.awt.MediaPictureConverter
import java.io.humble.video.awt.MediaPictureConverterFactory
import java.awt.image.BufferedImage

class Video {
    private val buffImgList: MutableList<BufferedImage>
    private val duration: Int // длительность видео
    private val fps: Int // количество кадров в секунду


    init {
        buffImgList = mutableListOf<BufferedImage>().apply {

        }
        duration = 120
        fps = 15
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

    fun createVideo(fileName: String, formatName: String, duration: Int, fps: Int) {
        val framerate = 1 / fps // кадровая частота

        val muxer = Muxer.make(fileName, null, formatName) // контейнер для файла
        val format = muxer.format
        val codec: Codec = Codec.findEncodingCodec(format.defaultVideoCodecId) // кодек, подходящий для нашего формата - устройство для преобразования данных

        val encoder = Encoder.make(codec) // создаём энкодер

        encoder.width = 300 // ширина кадров
        encoder.height = 100 // высота кадров

        val pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P
        encoder.pixelFormat = pixelFormat
        encoder.timeBase = framerate

        if (format.getFlag(ContainerFormat.Flag.GLOBAL_HEADER)) encoder.setFlag(Coder.Flag.FLAG_GLOBAL_HEADER, true) // устанавливаем флаги энкодера
        encoder.open(null, null)

        muxer.addNewStream(encoder)
        muxer.open(null, null)

        var converter: MediaPictureConverter? = null // конвертер для преобразования изображения к нужному формату
        val picture = MediaPicture.make(
            encoder.width,
            encoder.height,
            pixelFormat
        )
        picture.timeBase = framerate

        val packet = MediaPacket.make()
        var i = 0
        while (i < duration / framerate.toDouble()) { // кодируем изображения и добавляем в пакет

            buffImgList.forEach {
                if (converter == null) converter = MediaPictureConverterFactory.createConverter(it, picture)
                converter!!.toPicture(picture, it, i.toLong())
                do {
                    encoder.encode(packet, picture)
                    if (packet.isComplete) muxer.write(packet, false)
                } while (packet.isComplete)
                i++
            }
        }
        do {
            encoder.encode(packet, null)
            if (packet.isComplete) muxer.write(packet, false)
        } while (packet.isComplete)
        muxer.close()
    }
}