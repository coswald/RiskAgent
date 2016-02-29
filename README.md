# Risk Agent
Risky Business will design and implement an artificially intelligent method to play the table-top game called Risk. The project will be complete once the described agent can play Risk well and/or better than an expert player by implementing and intelligent algorithm called NEAT. We are doing this for those Risk players who feel that the AI's out there are not a real challenge and for those new players who want to play a Risk game as if they were playing it with their friends.
## Code Example
***TODO***
## Motivation
We felt that the current AI methods out there were not hard enough. This is due to the fact that they are all strategy based; given enough time and resources, one could predict how any given algorithm was to behave. We also felt that new players who do not have the time or friends to play with a group needed a good way to learn good strategies. In this endeavor we decided it best to implement our own AI in Risk.
## Installation
To install RiskAgent, you must purchase or already own the full version of the LuxDelux© software. This can be found at [SillySoft™'s website](http://sillysoft.net/lux/). Once obtained, installation is simple. Download and extract the zip or tarball into a temporary directory. Then copy the contents of the *bin* folder and move it to LuxDelux©'s *Agent* directory, found under the *SupportFolder* directory. More information on this and how Risk AI's work can be found at [Writing Your Own AI](http://sillysoft.net/wiki/?WritingYourOwnAI). Once this is completed, you can start LuxDelux©. If it was already running, you must restart LuxDelux©.
###Compiling
To compile this software, download the repository, extract it, and navigate to that directory. You must have Java 1.6 or Java 1.5 installed in order to compile. LuxDelux© only supports Java 1.6 and lower, as it uses Sun API. If Java 1.7 or later is installed and there is no way of going back (and you don't mind getting in some legal trouble), copy the SDK into the src directory. Then, edit the file called GraphicsTool. Comment the line (legal issue) that imports from SUN api. Now continue. If you haven't already, copy the Sillysoft SDK into the *src* folder, merging the com folders to make room for the sillysoft software. Make a diretory named *bin*, and make a *sources* file with all of the source files as the contents (with path to source files; include sillysoft sdk files). Then type "javac -d bin @sources". This will compile all of the files in the *sources* file into the *bin* directory. That should contain all of the necessary files to play RISK. Follow the steps above to install RiskyBusiness's agent.
## Tests
***TODO***
## Contributors
There are currently no way for outside groups to let us know about issues or how they can contribute. Once the project is complete, we will let you know of any way to do this.
## License
Make sure to look at [GNU's Public Liscense](http://www.gnu.org/licenses/gpl-3.0.en.html) page. Risky Business chose this license over any other as it is the most popular free copyright license out there.
