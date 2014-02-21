Ska LaLaLa
==========

Ska LaLaLa is a music composition/improvisation DSL written in Scala.

----------------------------------------------------------------------------------------------------

Interested in becoming creative with music but frustrated with the interface between
instrument and user, I've decided to write my first [DSL](http://en.wikipedia.org/wiki/Domain-specific_language)
using Scala. In other words, I found learning the guitar to be a barrier to
getting musical ideas out of my head, and since I already know code, I'd like to try
typing my music. A friend has argued that synthesizers can already do much of what I intend,
but they provide knobs, dials, and sliders instead of code! As a side-note, this same friend
is vearing in the opposite direction: looking for ever-more analogue solutions for recording,
looping, and mixing sound.

The first step is exploratory: understanding the Java Sound API and the music theory I'll need
to build a DSL on top of it.

After that, I will focus on building composed music. That is, rerunning the composition should
always produce the same audio stream.

After that I will focus on a REPL mode for improvisation: feedback loops, randomness, sampling, etc.