package io.gitlab.jfronny.javagi.example

import org.gnome.adw.Application
import org.gnome.adw.ApplicationWindow
import org.gnome.gio.ApplicationFlags
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Orientation

class Main {
    private val app: Application

    init {
        this.app = Application("org.gtk.example", ApplicationFlags.FLAGS_NONE)
        app.onActivate(org.gnome.gio.Application.ActivateCallback { this.onActivate() })
    }

    private fun onActivate() {
        val window = ApplicationWindow(app)
        window.setTitle("Hello")
        window.setDefaultSize(300, 200)

        val box = Box(Orientation.VERTICAL, 0)
        box.setHalign(Align.CENTER)
        box.setValign(Align.CENTER)

        val button = Button.withLabel("Hello, World!")
        button.onClicked(Button.ClickedCallback { window.close() })

        box.append(button)
        window.setContent(box)
        window.show()
    }

    private fun run(args: Array<String>): Int {
        return app.run(args)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(Main.javaClass.canonicalName)
            System.exit(Main().run(args))
        }
    }
}