### Peer Review Report:
Name - Sai Krishna Bollina <br>
Partner - Joshua Adams

We started by running through our applications; though most features are the same, we had two completely different approaches. We discussed the database part of our applications. Josh’s team followed the process where they had a compressed bitmap saved as a file, whereas we serialized the drawing data and saved the data in string format. 

While using the repository, I suggested Josh use the suspend functions and the worker threads to execute all the room queries on different threads, making things much more straightforward. Most of the time, you don't need to start tasks that outlive the function call, so this is perfectly fine. And there was an issue with the color picker that needs to be fixed.

In our app, as we are using serialized data, we discussed how it would be difficult for us in the future. We thought of adding a new feature of adding text/images in the drawing and as we cannot convert that into lines data we came up with a new implementation where we add a new data class which contains the drawing data in a form which has line data, other data like image name, coordinates on the canvas. So we will have a Image class and we will serialize like the same way we did for the line data and store it in the PaintsData as string.

In the testing part of our application we had to implement database related testing for the DrawScreen and as we were unable to figure out hoe to do a complete end to end UI + database testing we decided on doing it separately. We’ll be using dummy data to test out queries.

