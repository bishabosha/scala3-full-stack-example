package example

import upickle.default.{ReadWriter as Codec, *}

final case class Note(id: String, title: String, content: String) derives Codec

enum SubscriptionMessage derives Codec:
  case Delete(noteId: String)
  case Create(note: Note)
  case Update(note: Note)
