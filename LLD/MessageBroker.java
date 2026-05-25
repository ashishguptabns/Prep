/* A message broker is a bridge in Pub Sub model that allows communication between publishers
and subscribers without requiring the components to explicitly register with one another (and
thus be aware of each other). This allows publishers and subscribers to have independent
lifetimes and relatively independent scaling and capacity requirements.
Message broker accepts messages from publisher (usually one of a set of pre-defined topics).
Subscribers register with message broker using their endpoint configuration and the topic they
are interested in, to receive messages published to it . Message broker, then, ensures message
delivery to the subscribers. */