import org.gparallelizer.actors.pooledActors.SafeVariable

def jugMembers = new SafeVariable<List>(['Me']).start()  //add Me

jugMembers.send {it.add 'James'}  //add James

final Thread t1 = Thread.start {
    jugMembers.send {it.add 'Joe'}  //add Joe
}

final Thread t2 = Thread.start {
    jugMembers << {it.add 'Dave'}  //add Dave
    jugMembers << {it.add 'Alice'}  //add Alice
}

[t1, t2]*.join()
println jugMembers.val
jugMembers.valAsync {println "Current members: $it"}

System.in.read()
