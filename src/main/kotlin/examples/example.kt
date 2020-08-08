package examples

import MyView
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.stage.Stage
import tornadofx.*


class FxCanvasExample1 : App(MyView:: class) {
    override fun start(stage: Stage) {
        // Create the Canvas
        val canvas = Canvas(500.0, 500.0)
        val canvasTow = Canvas(200.0, 200.0)
        // Set the width of the Canvas
//        canvas.width = 800.0
        // Set the height of the Canvas
//        canvas.height = 800.0

        // Get the graphics context of the canvas
        val gc = canvas.graphicsContext2D
        val gcTwo = canvasTow.graphicsContext2D

        // Draw a Text
        gc.strokeText("Hello Canvas", 150.0, 100.0)
        gcTwo.strokeText("Hello Two Canvas", 150.0, 100.0)

        // Create the Pane
        val root = Pane()
        val rootTwo = Pane()
        // Set the Style-properties of the Pane
        root.style = "-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 15;" +
                "-fx-border-insets: 20;" +
                "-fx-border-radius: 1;" +
                "-fx-border-color: blue;"
        rootTwo.style = "-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 5;" +
                "-fx-border-insets: 7;" +
                "-fx-border-radius: 15;" +
                "-fx-border-color: red;"

        // Add the Canvas to the Pane
        root.children.add(canvas)
        rootTwo.children.add(canvasTow)
        // Create the Scene
        val scene = Scene(rootTwo)
        val sceneTwo = Scene(root)
//        // Add the Scene to the Stage
        stage.scene = scene
        stage.scene = sceneTwo
//        // Set the Title of the Stage
//        stage.title = "Creation of a Two Canvas"
//        // Display the Stage
        stage.show()
    }
}

fun main(args: Array<String>) {
//    Application.launch(MyView::class.java, *args)
    launch<FxCanvasExample1>(args)
}

