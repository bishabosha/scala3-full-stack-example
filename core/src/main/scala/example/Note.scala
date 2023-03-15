package example

import upickle.default.{ReadWriter as Codec, *}

final case class Note(id: String, title: String, content: String) derives Codec

sealed abstract class SubscriptionMessage derives Codec
object SubscriptionMessage:
  case class Delete(noteId: String) extends SubscriptionMessage derives Codec
  case class Create(note: Note) extends SubscriptionMessage derives Codec
  case class Update(note: Note) extends SubscriptionMessage derives Codec
