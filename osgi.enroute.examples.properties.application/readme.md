# OSGi ENROUTE EXAMPLES PROPERTIES APPLICATION

${Bundle-Description}

## Run

You can run the application by selecting osgi.enroute.examples.properties.bndrun. Resolve this bndrun file and then click on the Debug icon. You can then go to:

	http://localhots:8080/osgi.enroute.examples.properties

In the initial case you can not see any of the properties. You'll see the RUNVM property as being set to 'No RUN VM set'.

## Setting Runvm in the bndrun

Uncomment the `-runvm` line so it looks like:

	-runvm 		= -DRUNVM_HOME_IS_SET=1
	#-runvm.home = -DRUNVM_HOME_PLUS_IS_SET=1

Save, close the bndrun file, and open it again on the Run tab. Now stop the running framework and click on the Debug icon to restart the framework (these settings are not updated in the framework unlike the bundles). If you refresh the GUI then you should see the `RUNVM_HOME_IS_SET=1` property.

Uncomment the `-runvm.home` line so it looks like:

	-runvm 		= -DRUNVM_HOME_IS_SET=1
	-runvm.home = -DRUNVM_HOME_PLUS_IS_SET=1

Again, quit the bndrun editor and reopen [until we fixed this bug][1]. Then quit the running framework and start it again, then refresh. You now can see the 2 keys `RUNVM_HOME__IS_SET` and `RUNVM_HOME_PLUS_IS_SET`. You can see both because bnd merges all instructions that start with `-runvm`.

## Workspace Wide

You can also set the `-runvm` in `build.bnd`. This file is always at the top of your properties, projects, builders, and bndrun files inherit from this file. You find the following lines there:

	# For examples properties project
	#-runvm: -DRUNVM_IS_SET_IN_BUILD=1
	#-runvm.ext: -DRUNVM_BUILD_PLUS_IS_SET=1

If you uncomment these lines and restart (both the editor, then the framework, and then refresh the page) then you see the effects in the browser. Again, no refresh of all these parts and you're lost.

In the `cnf/ext` directory you can find the `examples.properties.bnd` file. All files in the `cnf/ext` directory are read before the `build.bnd` is read. Any properties in there, if not overridden later, will be available in the projects, bndruns, and builders. In the  `examples.properties.bnd`  file you can find the following lines.

	#
	# Try uncommenting these for testing the 
	# examples properties project
	#
	
	#-runvm: -DRUNVM_IS_SET_IN_EXT=1
	#-runvm.ext: -DRUNVM_EXT_PLUS_IS_SET=1

Again, play with uncommenting and then refreshing the browser. Did I tell you not to open and close the browser and restart the framework? 

## The Bug

You can follow the bug that requires this extensive refreshing [here][1]

[1]: https://github.com/bndtools/bndtools/issues/1010




