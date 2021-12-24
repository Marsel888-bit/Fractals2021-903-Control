package ru.smak.ui.painting

import io.humble.video.*
import io.humble.video.awt.MediaPictureConverter
import io.humble.video.awt.MediaPictureConverterFactory
import ru.smak.ui.painting.fractals.FractalPainter
import java.awt.image.BufferedImage
import kotlin.math.abs

class Video (private val painter: FractalPainter, private val selectablePanel: SelectablePanel) {
    private var keyFrames : ArrayList<Pair<BufferedImage, CartesianPlane>> = arrayListOf<Pair<BufferedImage, CartesianPlane>>()
    private var frames : ArrayList<BufferedImage> = arrayListOf<BufferedImage>()
    private val duration: Int // длительность видео
    private val fps: Int // количество кадров в секунду

    var secBetweenFrames : Int = 5
        get () = field
        set (value) {
            field = value
        }
    private val CountOfFrames
        get() = fps * secBetweenFrames
    fun addKeyFrames (_keyFrames : ArrayList<Pair<BufferedImage, CartesianPlane>>) {
        keyFrames = _keyFrames
    }

    init {
        duration = 10
        fps = 30
    }


    fun createVideo(fileName: String, formatName: String, duration: Int, fps: Int) {
        val framerate = Rational.make(1 , fps) // кадровая частота
        val muxer = Muxer.make(fileName, null, formatName) // контейнер для файла
        val format = muxer.format
        val codec: Codec = Codec.findEncodingCodec(format.defaultVideoCodecId) // ищем кодек, подходящий для нашего формата - устройство для преобразования данных

        val encoder = Encoder.make(codec) // создаём энкодер, главная задача которого - инкапсуляция информации о базовом формате данных
        val painter1 = painter
        encoder.width = painter1.plane.width // ширина кадров
        encoder.height = painter1.plane.height // высота кадров

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
        var j = 0
            while (j < duration / framerate.double) {
            keyFrames.forEachIndexed { i, kf ->
                if (i != keyFrames.size - 1) {
                    val currXmin = kf.second.xMin
                    val currXmax = kf.second.xMax
                    val currYmin = kf.second.yMin
                    val currYmax = kf.second.yMax
                    val xMaxRateOfChange = (currXmax - keyFrames.get(i + 1).second.xMax) / CountOfFrames // смещение для xMax
                    val xMinRateOfChange = abs(currXmin - keyFrames.get(i + 1).second.xMin) / CountOfFrames // смещение для xMin
                    val yMaxRateOfChange = (currYmax - keyFrames.get(i + 1).second.yMax) / CountOfFrames // для yMax
                    val yMinRateOfChange = abs(currYmin - keyFrames.get(i + 1).second.yMin) / CountOfFrames // для yMin
                    val _kf = kf
                    var subFrameNum = 0
                    while (j <= CountOfFrames * (i + 1)) {
                        _kf.second.xSegment =
                            Pair(currXmin + subFrameNum * xMinRateOfChange, currXmax - subFrameNum * xMaxRateOfChange)
                        _kf.second.ySegment =
                            Pair(currYmin + subFrameNum * yMinRateOfChange, currYmax - subFrameNum * yMaxRateOfChange)
                        val _painter = painter
                        _painter.plane.xSegment = _kf.second.xSegment
                        _painter.plane.ySegment = _kf.second.ySegment
                        val bufImg =
                            BufferedImage(_painter.plane.width, _painter.plane.height, BufferedImage.TYPE_INT_RGB)
                        val bufGraphics = bufImg.createGraphics()
                        _painter.paint(bufGraphics)
                        val image = BufferedImage(bufImg.width, bufImg.height, BufferedImage.TYPE_3BYTE_BGR)
                        image.graphics.drawImage(bufImg, 0, 0, null)
                        //selectablePanel.graphics.drawImage(bufImg, 0, 0, null)
                        if (converter == null) converter = MediaPictureConverterFactory.createConverter(image, picture)
                        converter!!.toPicture(picture, image, j.toLong())
                        do {
                            encoder.encode(packet, picture)
                            if (packet.isComplete) muxer.write(packet, false)
                        } while (packet.isComplete)
                        j++
                        subFrameNum++
                    }
                }
                do {
                    encoder.encode(packet, null)
                    if (packet.isComplete) muxer.write(packet, false)
                } while (packet.isComplete)
                muxer.close()
            }
        }
    }


}