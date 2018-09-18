# Online-Dictionary
The Distributed System Assignment 1 - Online Dictionary

Overview

A Multi-threaded dictionary server called xDictionary and  a GUI client application are implemented. It allows concurrent clients to query the senses of words, add a new word, and remove an existing word through sockets by using TCP. The threadpool technology has been used to implement the concurrency on multi-threads, and the thread interference and memory consistency errors are prevented by using synchronized method and ConcurrentHashMap in Java. 

In the server, a hash map in-memory dictionary data structure has been estiblished to handle with the get, put, and remove functions and the saved dictionary file is Extensible Markup Language (XML) format. XStream, a simple XML parser library, is utilized to serialize objects to XML and vice versa. A RESTful API of Oxford dictionary has been included in order to get the senses of a word when it does not exist in server’s dictionary data. Meanings and examples of the given word are obtained from a JavaScript Object Notation (JSON) format output stream by using Jackson, a JSON parser library.

The Graphical User Interface (GUI) client application is developed on JavaFX. The results of requests could be saved in a XML file by using Java Architecture for XML Binding (JAXB). Furthermore, the functions of querying the senses of a given word, adding a new word, and removing an existing word, and corresponding input validating and errors/exceptions handling have been implemented.
