package io.github.gaming32.sortviskt

import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO
import kotlin.random.Random

// Huge credit to Notch for creating this text renderer for Minecraft

const val FORMAT_CODE = '\u00a7'

object TextRenderer {
    private val charWidth = IntArray(256)
    private val colorCodes = IntArray(32)
    private val allowedChars: String

    private var fontTex = 0
    private var x = 0f
    private var y = 0f

    init {
        val imageWidth: Int
        val imageHeight: Int
        val pixelData: IntArray
        ImageIO.read(javaClass.getResource("/default_font.png")).also { fontImage ->
            imageWidth = fontImage.width
            imageHeight = fontImage.height
            pixelData = IntArray(imageWidth * imageHeight)
            fontImage.getRGB(0, 0, imageWidth, imageHeight, pixelData, 0, imageWidth)
        }

        for (k in 0 until 256) {
            val i1 = k % 16
            val k1 = k / 16
            var i2 = 7
            while (i2 >= 0) {
                val k2 = i1 * 8 + i2
                var flag1 = true
                var j3 = 0
                while (j3 < 8 && flag1) {
                    val l3 = (k1 * 8 + j3) * imageWidth
                    val j4 = pixelData[k2 + l3] and 0xff
                    if (j4 > 0) {
                        flag1 = false
                    }
                    j3++
                }
                if (!flag1) break
                i2--
            }
            if (k == 32) {
                i2 = 2
            }
            charWidth[k] = i2 + 2
        }

        for (l in 0 until 32) {
            val j1 = (l shr 3 and 1) * 85
            var l1 = (l shr 2 and 1) * 170 + j1
            var j2 = (l shr 1 and 1) * 170 + j1
            var l2 = (l shr 0 and 1) * 170 + j1
            if (l == 6) {
                l1 += 85
            }
            if (l >= 16) {
                l1 /= 4
                j2 /= 4
                l2 /= 4
            }
            colorCodes[l] = (l1 and 0xff shl 16) or (j2 and 0xff shl 8) or (l2 and 0xff)
        }

        allowedChars = javaClass.getResourceAsStream("/font.txt")?.let {
            BufferedReader(InputStreamReader(it, StandardCharsets.UTF_8))
        }?.useLines { it.joinToString(separator = "") { line ->
            if (line[0] == '#') ""
            else line
        } } ?: ""
    }

    fun drawText(s: String, x: Float, y: Float, color: Color) = drawText(
        s, x, y,
        (color.alpha shl 24) or (color.red shl 16) or (color.green shl 8) or color.blue
    )

    fun drawText(s: String, x: Float, y: Float, color: Int) {
        if (fontTex == 0) loadFont()
        var colorI = color
        if ((colorI and 0xff000000.toInt()) == 0) {
            colorI = colorI or 0xff000000.toInt()
        }
        glColor4f(
            (colorI shr 16 and 0xff) / 255f,
            (colorI shr 8 and 0xff) / 255f,
            (colorI and 0xff) / 255f,
            (colorI shr 24 and 0xff) / 255f
        )
        this.x = x
        this.y = y
        drawText(s)
    }

    fun getStringWidth(s: String): Int {
        var width = 0
        var ix = 0
        while (ix < s.length) {
            val c = s[ix]
            if (c == FORMAT_CODE) {
                ix += 2
                continue
            }
            val charIndex = allowedChars.indexOf(c)
            if (charIndex != -1) {
                width += charWidth[charIndex + 32]
            }
            ix++
        }
        return width
    }

    fun drawCenteredText(s: String, x: Float, y: Float, color: Int) =
        drawText(s, x - getStringWidth(s) / 2, y, color)

    fun drawRightText(s: String, x: Float, y: Float, color: Int) = drawText(s, x - getStringWidth(s), y, color)

    private fun loadFont() {
        fontTex = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, fontTex)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP)

        val font = ImageIO.read(javaClass.getResource("/default_font.png"))
        val width = font.width
        val height = font.height
        val rgb = IntArray(width * height)
        font.getRGB(0, 0, width, height, rgb, 0, width)

        val rgbBytes = ByteArray(width * height * 4)
        for (i in rgb.indices) {
            val pixel = rgb[i]
            val a = pixel shr 24 and 0xff
            val r = pixel shr 16 and 0xff
            val g = pixel shr 8 and 0xff
            val b = pixel and 0xff
            rgbBytes[i * 4 + 0] = r.toByte()
            rgbBytes[i * 4 + 1] = g.toByte()
            rgbBytes[i * 4 + 2] = b.toByte()
            rgbBytes[i * 4 + 3] = a.toByte()
        }

        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
            ByteBuffer.allocateDirect(rgbBytes.size).order(ByteOrder.nativeOrder()).put(rgbBytes).flip() as ByteBuffer
        )
    }

    private fun drawText(s: String) {
        var obfuscated = false
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == FORMAT_CODE && i + 1 < s.length) {
                val commandC = s[i + 1].lowercaseChar()
                if (commandC == 'k') {
                    obfuscated = true
                } else {
                    val commandI = "0123456789abcdefk".indexOf(commandC)
                    obfuscated = false
                    val newColor = colorCodes[if (commandI == -1) 15 else commandI]
                    glColor3f(
                        (newColor shr 16 and 0xff) / 255f,
                        (newColor shr 8 and 0xff) / 255f,
                        (newColor and 0xff) / 255f
                    )
                }
                i += 2
                continue
            }
            var charIndex = allowedChars.indexOf(c)
            if (obfuscated && charIndex > 0) {
                var newCharIndex: Int
                do {
                    newCharIndex = Random.nextInt(allowedChars.length)
                } while (charWidth[charIndex] == charWidth[newCharIndex])
                charIndex = newCharIndex
            }
            if (c == ' ') {
                x += 4
                i++
                continue
            }
            if (charIndex > 0) {
                 renderNormalChar(charIndex + 32)
            }
            i++
        }
    }

    private fun renderNormalChar(c: Int) {
        val texX = (c % 16) * 8
        val texY = (c / 16) * 8
        val cWidth = charWidth[c] - 0.01f
        glBindTexture(GL_TEXTURE_2D, fontTex)
        glBegin(GL_TRIANGLE_STRIP)
        glTexCoord2f(texX / 128f, texY / 128f)
        glVertex2f(x, y)
        glTexCoord2f(texX / 128f, (texY + 7.99f) / 128f)
        glVertex2f(x, y + 7.99f)
        glTexCoord2f((texX + cWidth) / 128f, texY / 128f)
        glVertex2f(x + cWidth, y)
        glTexCoord2f((texX + cWidth) / 128f, (texY + 7.99f) / 128f)
        glVertex2f(x + cWidth, y + 7.99f)
        glEnd()
        x += charWidth[c]
    }
}
