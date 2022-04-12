# Kanastruk public code repo

Here lives some of Kanastruk public-facing code. Specifically companion content for our learning videos or our blog posts. 

Each sub-directory is it's own project. Idea is to be able to setup multiple github repositories.

## Projects

- `/core/kmpp`: Companion code for Kotlin Multiplatform intro video. Some gotchas:
	- You'll need to set up your own Firebase backend to run this code.
	- I've not yet gotten around to writing a Firebase setup tutorial.
	- If you're familliar with Firebase you should be able to manage. 
		- `androidApp` was running straight off a live Firebase setup.
		- `jsApp` should auto-detect a local Firebase emulator instance. 
	- If you have setup questions, you can ask them via GitHub Discussions, or Issues.
- `/firebase/kmpp`: Provides minimal auth / realtime-database capability for kmpp examples.

## Companion projects

If we built out any stand alone project, we'll list them here. None for now.

