webin-data-streamer-Client
==========================

This project extends the ftp4j FTP Client project by replacing Bromo's TCP sockets with hybrid TCP/UDT sockets. It works exactly like Bromo, however it allows the FTP data connection to utilize the UDT protocol now as well.

Version 0.7

To build the server from source, change to working directory webin-data-streamer-Client/

ant build package-for-store

This generates the single-jar product 'webin-data-streamer-Client.jar' in the store/ directory. Abt version >= 1.8.0 is required.
