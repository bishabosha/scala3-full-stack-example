package example

import org.scalajs.dom.html.Element
import org.scalajs.dom.document
import org.scalajs.dom.html.*

import DomHelper.*

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object WebPage:
  given ExecutionContext = ExecutionContext.global

  val activeNotes = collection.mutable.Map.empty[String, Element]

  val service = new HttpClient()

  val titleInput = input()
  val contentTextArea = textarea()

  val saveButton = button("Create Note")
  saveButton.onclick = _ =>
    service
      .createNote(titleInput.value, contentTextArea.value)
      .map(addNote)

  val form: Div = div(
    titleInput,
    contentTextArea,
    saveButton
  )
  form.className = "note-form"

  val appContainer: Div = div(
    h1("My Notepad"),
    form
  )
  appContainer.id = "app-container"

  def scanNotesLoop(timeout: Int): Unit =
    for notes <- service.getAllNotes() do
      val current = activeNotes.keys.toList
      for
        id <- current
        if !notes.exists(_.id == id)
      do
        deleteNote(id)
      for
        note <- notes
        if !activeNotes.contains(note.id)
      do
        addNote(note)
      // org.scalajs.dom.window.setTimeout(() => scanNotesLoop(timeout), timeout)

  def deleteNote(id: String): Unit =
    activeNotes.updateWith(id) {
      case Some(elem) =>
        appContainer.removeChild(elem)
        None
      case None => None
    }

  def addNote(note: Note): Unit =
    val elem = div(
      h2(note.title),
      p(note.content)
    )

    val deleteButton = button("Delete Note")
    deleteButton.onclick = _ =>
      service
        .deleteNote(note.id)
        .map(res =>
          if res then deleteNote(note.id)
        )

    elem.appendChild(deleteButton)
    elem.className = "note"
    activeNotes(note.id) = elem
    appContainer.appendChild(elem)

  @main def start: Unit =
    document.body.appendChild(appContainer)
    scanNotesLoop(300)
