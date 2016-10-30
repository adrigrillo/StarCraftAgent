# PATHING STRATEGIES
## pathing 1: intro

I don’t know much about pathfinding, so I decided to write about it.

I wrote earlier about threat-aware pathing, but I didn’t know how to implement it efficiently. Bots are real time and have a lot to think about, so faster is better.

I’ll write about classic A\* pathing and its descendants, about potential fields, and about any other interesting ideas my shovel turns up (I’ve seen the corners of a couple other things that might be cool). I’ll look into the pathfinding features of libraries like BWEM. I hope to learn something about how it all works at the low level with BWAPI and how it might interact with Starcraft’s built-in pathfinding. In the end I’ll try to put pieces together to outline how to find paths in full generality, taking into account strategic and tactical goals, obstructions like terrain, destructible map objects, buildings and units, and fields of vision and fields of fire for both sides. I’ll try, but I don’t promise to succeed!

This should be old hat for old hands, but a lot of it is new to me. Maybe I’ll get some help in the comments.

## pathing 2: the classic A\* algorithm

The A\* algorithm (pronounced “A star”) is famous in AI. It’s a general-purpose best-first graph search algorithm, and finding paths is only one of its uses (though the biggest).

The Wikipedia article makes it sound more complicated than it is. If you need an introduction from zero, I thought a better source was A\* Pathfinding for Beginners by Patrick Lester, from 2005 (though some further reading links are broken now).

The situation: You start at a node in a graph. In the graph, every arc between nodes gives the distance between them. Somewhere out there in the graph are goal nodes. You want to find the closest goal node, or at least one of them. Luckily you don’t have to search blindly, because you have a heuristic function h(x) which gives you a guess, for each node x, telling how close it may be to a goal. Of course h(x) = 0 when x is a goal node.

The algorithm: You keep an “open list” of nodes that are in line to be searched. For each node x in the open list you remember its distance from the start, which is traditionally called g(x). The open list starts out containing the start node, with distance 0 from itself. Each search step is: From the open list, pick the node x with the lowest value g(x) + h(x), the one which is estimated to be closest to a goal (that’s what makes it a best-first algorithm). g is how far you have come, h is how far you estimate you have to go, their sum is the estimated total distance. If x is a goal, done. Otherwise remove x from the open list and add any of x’s neighbors that have not already been visited (a node that is or ever was on the open list has been visited). That’s all there is to it; code may be filled out with special-case improvements or implementation details, but the idea is that simple.

Because you always take the best node from the open list, the open list can be implemented as a priority queue. Different ways of breaking ties in the priority queue give different search behavior. All the variants are called A\*.

What is the mysterious heuristic function h(x)? If h is the exact distance to the goal, then A\* wastes no time on side paths and proceeds straight to the goal. But if you had that, you wouldn’t need A\*. If h never overestimates the distance to the goal, then A\* is guaranteed to find the shortest path: It took what it thought was shortest at each step, and may have made optimistic mistakes but never overshot, so it could not have overlooked a shorter path.

So for pathfinding on a map, it generally works to set h(x) = the straight line distance between the start and end points. The actual distance to the goal may be longer than the straight-line distance, but never shorter, so you’ll find the best path. There may be smarter heuristics that know about map regions or obstacles, but they’re not obvious (and not for this post).

A\* is mathematically optimal in a certain sense; can can call it “the fastest” algorithm that solves its exact problem. But don’t be fooled. You may be able to do better than A\* by solving a different problem. If you imagine pathfinding on a featureless grid with no obstacles, you can calculate a straight-line path from the start to the goal without examining any nodes in between, because you already know all about them—you’re solving an easier problem and you can do it in one step. There may be ways to cast Starcraft pathfinding as an easier problem than graph search (I’m pretty sure there are), so A\* is not necessarily optimal for us.
A\* is not able to solve pathfinding in full generality. Make each map tile a graph node. A tile blocked by a building or destructible map obstacle can be given a larger distance from its neighbors to represent the time it takes to clear the obstacle. You can even represent that tiles which are under enemy fire are unsafe to travel over by making the distance to those tiles longer, so that other paths are preferred when available. But A\* assumes that the graph is static. It can’t cope with other units moving around or with buildings being built or lifting off. Starcraft is too dynamic for A\* to solve the whole range of pathfinding problems. A\* in its basic form can only do part of the job.

## pathing 3 - hierarchical algorithms based on A\*

As the next step in tracing the development of A\* methods, I picked this older paper from 2004 by Adi Botea, Martin Müller, and Jonathan Schaeffer of the games group at the University of Alberta:

Near Optimal Hierarchical Path-Finding

Links to code in the paper are expired, unsurprisingly. The presented algorithm seems OK to me (it’s about saving time by accepting a slightly worse path), but I was most interested in the literature review from pages 5-8.

They talk about a bunch of algorithms based on A\* but in some way more efficient. These fancier algorithms are hierarchical, with at least two levels of abstraction. If there are two levels, then the higher level is something like “go through these map regions” (maybe giving border points to pass through), and the lower level is more like “go through these points within a region.” Finding a path means doing a hierarchical search to find a high-level path (“go through these regions”) and a low-level path (“go through these map grid points”). Each level may be an A\* search itself.

Note: This is not the same kind of hierarchical search that I promised to talk about, though it’s related. Pathfinding hierarchies have only a simple form of abstraction.

My conclusions:

- The hierarchical search, if it has two levels, goes something like this: The top-level search plans a path through map regions. To find the cost of traversing a region (“go from this choke to that one”), it calls on the low-level search. The low-level search should cache answers, because it’s going to be asked the same questions frequently. Details vary by algorithm but seem easy enough to figure out.

- Maps are dynamic. Not everything stays put. In Starcraft, blocking minerals can be mined out and map buildings can be destroyed. Your own and the opponent’s buildings and units act as physical obstacles too. For full accuracy, the low-level search has to take everything into account. Ideas I’ve seen so far for coping with this within the A\* family seem to amount to “replan if the map changes,” which is OK for occasional changes like the destruction of map obstacles.

- Be lazy. Not only maps are dynamic, goals are dynamic too; before you reach the end of your path, you may change your mind and want to go somewhere else after all. So don’t spend cpu time to plan the whole path in full accuracy. Make sure the high-level path is good and plan only your immediate moves in full accuracy. One idea is to have a quick low-level search that only takes into account map features and a full-accuracy low-level search that takes everything into account.

- Group movement is important in Starcraft, and this paper doesn’t talk about it. You usually want your units together (not straggling separately past obstacles, or taking different bridges when they might find trouble before joining up again), and if the enemy is around then you care about good formation. That deserves another post.

## pathing 4 - up-to-date A\* stuff

For a view of the A\* family today, I liked this site the best. Maybe I should have skipped the last two posts and pointed “go there!” But I don’t regret stopping at a couple points along the road to get an idea of the historical development.

Practical techniques. Amit talks about different choices of heuristic, ways of adjusting the “actual” sunk cost g(x) and/or the heuristic future cost h(x) to get different behavior, and other little tricks. Worth reading, not worth repeating.

Implementation details. Amit has a long section on how to make A\* efficient. For the priority queue at the heart of A\*, he examines the tradeoffs of too many algorithms and then says that he’s never needed anything other than a binary heap. OK, done!

Various fancier data structures. This seems like a key section. If you represent a Starcraft map as a grid of tiles, then A\* will have to do a ton of work to step over each tile that might be in a path. Amit offers these choices that seem relevant to Starcraft.

- visibility graph - draw polygons around obstacles and navigate from corner to corner
- navigation mesh - break walkable areas into convex polygons and navigate from edge to edge
- hierarchical representations - where each hierarchy level may be of a different type
- skip links - add extra graph edges to take long paths in one A\* search step (cheap imitation hierarchical representation)
And there are choices about how to use each representation. It an important topic, but I don’t know enough to judge which ideas are worth going into in detail. I’ll come back to it when I’ve gotten further along.

Path recalculation when the map changes on you, ditto: I’ll revisit if necessary when I know more.

Islands. If the map has areas you can’t reach by walking but you may want to go to, you’d better mark them as only reachable by air. Do a preprocessing step, I guess, or at least cache the results. You don’t want to have to repeatedly search the whole map during the game to find out that you can’t walk there. That includes not only islands to expand to but cliffs you might want to drop on. Obvious, but I hadn’t thought of it.

Group movement more or less says “see flocking.” I’ll do flocking after potential fields, since they’re related. Is it Opprimobot that claims to use a flocking algorithm? Some bot does; I’ll check it out.

Coordinated movement, like moving in formation or moving through a narrow passage in order, earns a paragraph. It’s potentially interesting for Starcraft. If you want to do that coordination in top-down planning style, a better source is the article pair Coordinated Unit Movement and Implementing Coordinated Movement by Dave Pottinger on Gamasutra.

## pathing 5 - potential fields

Pathfinding algorithms come in two kinds, planning algorithms and reactive algorithms. The planning algorithms are the A\* gang, which rely on predictions of future events to find good ways forward. When something unpredictable happens (and the enemy will try to make sure that it does), planners have to spend time replanning. Because A\* doesn’t try to predict enemy actions, the original plan may have been poor in the first place.

Potential fields are a family of reactive algorithms, or more precisely a data structure and framework for making up reactive navigation algorithms. Reactive algorithms, popularized in robotics by Rod Brooks, look at the immediate situation and react to it. They don’t know or care what happens next, they just try to do something sensible in the moment.

Potential fields seem popular; a lot of bots say they use them. My favorite sources are by Johan Hagelbäck. Ratiotile pointed out the thesis in this comment (thanks!).

A Multi-Agent Potential Field based approach for Real-Time Strategy Game Bots - 2009 thesis with a simpler game than Starcraft
Potential-Field Based navigation in StarCraft - 2012 paper covering OpprimoBot

The idea is to define a potential for each map location (x, y). For its next move, the unit seeks the highest potential in its immediate neighborhood (or, if you think like a physicist, rolls downhill toward the lowest potential). The goal generates an attractive potential that reaches across the map. Obstacles generate a short-range repulsive potential. And so on—the Starcraft paper has a table of OpprimoBot’s sources of potential. To get the overall potential, the separate potentials from each origin are combined, by adding or with max depending on the effect.

For efficiency, you don’t calculate the full potential field across the map. For each unit, calculate the potential for a small selection of points where you may want to move next. Also, use a bounding box (or something) to prune away sources of potential which are too far away to matter.

Advantages. The strengths of potential fields cover the weaknesses of A\*. They naturally handle collisions and destructible obstacles and dynamic hazards and even combat behavior. The thesis suggests a number of tricks for different situations.

It’s easy to think about and easy to implement. The problem shifts from “what behavior do I want in this complex situation?” to “what potential should each object generate?” The problem decomposition is given for you, if you like. You still have to solve the problem, giving attractive fields to goals (“attack the enemy here”) and repulsive fields to hazards (“combat simulator says you’re going to lose, run away”).

Simple micro is easy. To get kiting, give enemies an attractive potential field (up to just inside your firing range) when you’re ready to fire and a repulsive one during cooldown. Some bots overdo it and kite, for example, dragoons against overlords; there’s no need to avoid units which can’t hurt you (and it can reduce your firing rate).

Simple cooperative behavior falls out. If your tanks avoid each other as obstacles and are attracted to within siege range of enemies, then they’ll form up into a line at the right range.

Complicated behavior is possible. Nothing says that a reactive algorithm has to be 100% reactive—a planner can calculate potential fields too, any kind of potential field for any purpose. You can, say, scan the enemy base and use a planner to decide where each dropship should go, and generate a potential field to pull them to their targets. The dropships will dodge unexpected enemies on their way, if the enemies generate their own fields. Potential fields are general-purpose.

Problems. The famous problem is that the fields may combine in a way that creates a local optimum where a unit gets stuck permanently. OpprimoBot reduces but does not solve the local optimum problem with a “pheromone trail,” meaning that each unit’s own backtrail generates a repulsive field that keeps the unit moving. IceBot uses a different solution, called potential flow, which mathematically eliminates the possibility of local optimum points. One paper is Potential Flow for Unit Positioning During Combat in StarCraft (pdf) by IceBot team members, 2013.

It may not be easy to tune the potential fields to get good overall behavior. OpprimoBot apparently has hand-tuned fields. The Berkeley Overmind used machine learning to tune its potential fields.

In general, the weakness of potential fields is that they don’t foresee what happens next. The space between these enemy groups is safe—until you get sandwiched. The Berkeley Overmind used to compute the convex hull of the threats to avoid getting caught inside (and then fly around the outside looking for weak points). But that’s only one example of a way to get into trouble by ignoring what’s next. The Starcraft paper gives a simpler example: If the base exit is to the north and the enemy is to the south, units won’t get out.

The bottom line is that potential fields look great for local decision-making, and not so great for larger decisions like “which choke do I go through?” Which immediately suggests that you could use A\* to plan a path at the map region level (“go through this sequence of chokes”) and potential fields along the way. OpprimoBot’s documentation says that it uses Starcraft’s built-in navigation for units which have no enemy in sight range, and otherwise switches to potential fields or flocking.

I see obvious ways to combine potential fields with forward search to gain advantages from both, and that may go into the final Pathfinder Almighty. I think we knew from the start that the Pathfinder Almighty, which takes all available information into account, was neither a pure planning algorithm (since it expects surprises) nor a pure reactive algorithm (since it has to foresee the range of enemy reactions).

## pathing 6 - flocking

Until the 1980s, most scholars believed that in a flock of birds, or a school of fish, or a herd of antelopes, one animal was the leader and the group moved together because they followed the leader—even though nobody could figure out which animal it was.

Today we know that animal groups commonly self-organize by simple rules. Craig Reynolds’ boids flocking rules from 1986 are still the prototype today: Each boid looks at its local flockmates (and nothing else) and aims for separation from them to avoid crowding, alignment with the direction they’re heading, and cohesion, or keeping toward the center of mass so the flock holds together.

Flocking algorithms are reactive algorithms for local collision avoidance and group behavior, not very different in idea from potential fields. From what I read, it seems common in games to use A\* for pathfinding plus something in the flocking family for steering along the way, where “steering” means detailed movement in reaction to the immediate surroundings.

OpprimoBot can be configured to use a boids algorithm as an alternative to potential fields, and the boids algorithm is turned on for SSCAIT, supposedly because it performs better. So I looked at the source in NavigationAgent::computeBoidsMove() which is called to move one unit a step toward a chosen destination.

Lotsa stuff in there! It’s a long sweep of straight-line code and easy to read. It computes dx and dy relative to the current position of a unit and decides whether to move or attack, and then records the order it computed: From the current position (x, y) to (x+dx, y+dy). I see these steps, in order:

add a delta in the direction of the specified goal
add a delta toward the center of mass of the squad (cohesion)
add a delta tending to keep squad units moving in the same direction (alignment)
for ground units only, add a delta away from the closest squadmates if they’re too close (separation)
make a more complicated check about whether to retreat from enemies; if so, add a delta for that
add a delta away from terrain obstacles that are too close
if retreating, record a move to the new location, otherwise an attack move
I think the retreat implements kiting for ranged units and escape for units that have no attack. I couldn’t find any case where one delta depended on earlier ones, so I think they’re independent and the order doesn’t matter.

That seems simpler overall than potential fields, and OpprimoBot’s author Johan Hagelbäck seems to believe it works better. It certainly doesn’t have the local optimum problem.

OpprimoBot’s boids movement algorithm produces only the simplest attack-move micro. If you want something fancier, it seems to me that a flocking algorithm could be adapted to create movement toward weakly-defended targets, flight from strong enemies, and focus-fire to kill chosen enemies efficiently. Or you could say that’s no longer pathfinding and code it separately.

## pathing 7 - terrain analysis libraries

Historically, the BWTA library (for Brood War Terrain Analysis) was the solution. Here’s what I learned about the situation nowadays.

roll your own

Of the 13 bots whose source I had handy, these do not use BWTA but apparently roll their own map analysis: MaasCraft, Skynet, Tscmoo. In some cases it may be a vote of no confidence in the BWTA library; in some it may mean that that author wanted to do it on their own. UAlbertaBot uses BWTA for some tasks and its own map analysis for others, and likely some other bots do too.

Ratiotile has a sequence of 6 posts from 2012 analyzing region and choke detection in Skynet and BWTA and reimplements Skynet’s algorithm with tweaks.

BWTA2

The BWTA2 library in C++ is the maintained version of the abandoned original BWTA. I assume this is the version that BWMirror for Java “mirrors”.

I see complaints online that BWTA was slow and crashed on some maps. BWTA2 should at least reduce those problems.

It looks like BWTA2 does pathfinding using a navigation mesh, one of the fancy algorithms that I left for later consideration in this post. I’m thinking that I won’t return to the fancy algorithms because they’re not necessary for Starcraft. It’s easy to guess how a complex data structure leads to a slow and buggy map analysis.

BWEM

The BWEM C++ library (for Brood War Easy Map) by Igor Dimitrijevic is an alternative to BWTA. It claims to provide comparable information and to be more reliable and much faster. It also lets you send events to register that a map building or mineral patch has been removed, and updates its information to match.

I’m not aware of any bot that uses BWEM other than Igor Dimitrijevic’s own Stone and Iron, but I wouldn’t be surprised to find out that a few new bots do. BWEM 1.0 was released in September 2015, and BWEM 1.2 in February.

BWEM calculates ground distances and similar info in O(1) time using grids of data that are written by simple flood-fill algorithms. It’s similar to how Skynet does it (I haven’t looked into MaasCraft or Tscmoo). It makes sense that it should be fast and reliable.

For pathfinding specifically, BWEM returns a high-level path as a list of chokepoints to pass through. Paths are precomputed when the map is analyzed, so the operation is a lookup. If you want low-level pathfinding too, you need to do it another way.

BWEM knows about mineral patches which block the building of a base, usually a base on an island. Base::BlockingMinerals returns a list of blocking minerals for a given base, so you can mine them out first.

what to do?

Take advice about what library to use from people who have used them, not from me. But if I were evaluating them, I’d start with BWEM as the more modern choice.

## pathing 8 - what is pathfinding?

Or rather, what should we want pathfinding to be? The term “pathfinding” prejudices the issue: It means finding a path from here to a given goal. The term presupposes that selecting the goal and figuring out how to get there can be separated. Picking the goal depends on the game situation and on what the enemy might do. Choosing how to get there also depends on the game situation and on what the enemy might do. The goal you want may depend on which paths the enemy is likely to block or to have vision over, so goal finding and path finding ought to feed into each other. Maybe we should use the word “movement” instead.

Following a static path to move blindly to a goal is a way to make terrible mistakes.

Consider some use cases:

• Scouting. The scout doesn’t mind being seen, as long as it gets to see too, but it wants to live as long as possible to see more. Later in the game you may see the most by reconnaissance in force, that is, scouting with the army. The most interesting targets to scout can change with new information: “There’s an expansion here, there shouldn’t be one over there too.” Or: “These units are screening a base that used to be empty; is it still empty?” The best scouting takes into account possible enemy intentions and tells you actual enemy intentions.

• Defending against an ongoing attack. It’s not enough to arrive at the goal, you have to arrive in position to help. It’s a tactical calculation about how reinforcements can best coordinate with existing defenders. Do I need to retreat until I can gather enough units to hold the attack? Can I hold long enough for the reinforcements to make a sandwich?

• Reinforcing an army with new production. Sometimes you can send new units to the front and it’s a pathfinding operation. Sometimes the enemy intercepts your reinforcements and it becomes a tactical calculation.

• Targets of opportunity. When a squad spots a target it can attack with little risk, it has to decide whether the original goal or the new target is better. Maybe other units should be rerouted to join in.

• Air units. Air units can fly anywhere and don’t need pathfinding to reach a goal, but they still prefer some paths over others. If the enemy is near, air units often want an escape route: A nearby obstacle or friendly force to retreat behind when in danger. Mutalisks, corsairs, and wraiths can rely on their speed to escape, but they still want to watch enemy movements to make sure they don’t get cut off and trapped. Overlords want to be close enough to see and detect as necessary, but safe and ideally unseen themselves.

• Dropping. The transports would like to stay unobserved until as late as possible, to reduce the enemy’s reaction time: Plan a path over friendly territory, above cliffs, at the extreme edge of the map, and so on. If there is no good path, then the drop target may be a poor one. After being seen, the transports have to circle around or thread through static defenses and moving defenders. If the defense is too strong, the drop should turn back, maybe unloading some units early to save what can be saved. It’s a matter of weighing risk and benefit, both of which are uncertain and depend on the overall game situation and on what the transports see immediately around them.

I’m beating a dead horse by now. Whether the goal is good depends on whether the path is good, and you have to reevaluate the goal when new information comes up en route. Tactical analysis and path analysis have to be integrated one way or another. And the enemy gets a vote, so A\* is not a natural fit; A\* thinks it is the only agent in the world. If you plan movement by search, it’s natural to use some kind of adversary search.

MaasCraft’s tactical search is one way of integrating tactical analysis and path planning. It’s too coarse-grained to catch all the nuances, but I think it’s an excellent start.

Another consideration is information. You do sometimes have to plan long paths, even if you don’t always follow them to the end. You have good information about the start of the path, because you can see it. You have less information about the end of the path, because a lot may have changed by then. It may make sense to plan the near segment of the path in detail (if you don’t handle details with a reactive algorithm), and distant segments only coarsely.

## pathing 9 - the Pathfinder Almighty

I’ll outline vaguely more or less how a bot could handle movement in all circumstances, taking everything into account. You knew from the start this wasn’t going to be anything practical, right? PerfectBot might have this kind of system; it’s not something to sit down and implement, it’s a destination to approach over time (if we can find the path). “Pathfinder Almighty” is of course a funny name, since if you manage to create it, it won’t be either of those things. It will be a tactical analysis system that can sometimes achieve its goals!

design wishes

Understand blocking by buildings.
Don’t lock yourself in by mistake.
Know what to do about your own and enemy walls.
Know about narrowing chokes to restrict runbys.
Know how buildings increase path lengths.
Know how many units still fit in the space (for moving groups).
Understand blocking by units.
Pass through narrow chokes smoothly (at least don’t get clogged up).
Don’t (say) siege on a ramp if other units want to move up.
Use units to physically block (say) zealots from reaching tanks.
Handle units in stasis, or under maelstrom, or locked down.
Account for possible enemy vision, enemy fire, enemy movement.
Account for uncertainty.
Minimum effort. Spend time planning only what benefits from being planned.
As I mentioned last time, uncertainty is small in the immediate future and grows with time.

I haven’t noticed any bot that seems to understand blocking with units. It would allow a lot of valuable skills, such as:

probe body-blocks the scouting SCV to slow it so the zealot can hit
block the ramp to delay attacks
with zealots until dark templar defense is up
with dark templar
with eggs
with stasis or maelstrom or lockdown
medics or SCVs block zerglings from reaching marines
vultures lay mines among dragoons and physically block their escape
vultures physically block zealots from reaching tanks
modeling the world

By the minimum effort and uncertainty management principles, we want to model the immediate situation (where you start from now, the stuff you can see) in detail and the distant situation (where you want to end up) only roughly, and probably with measures of uncertainty. “Enemy army is not in sight, might be close, but is most likely guarding their choke.”

So model the enemy locations that you can see as units in exact positions, and those in distant or future places maybe as an influence map or a risk map.

decision making

Here’s what seems to me a good way to divide up the work. Let there be a tactics boss whose job it is to give orders to squads, and a unit boss whose job it is to carry out those orders by issuing detailed orders to units. Orders to a squad are from some list similar to this: Scout a location, move safely to a location (e.g., transfer workers), attack an area/units (go for the enemy), defend an area (e.g. your own mineral line from harassment), hold a line against attack (stop the enemy at a choke), drop a location, assume a formation. Or something along those lines. The tactics boss should be responsible for ordering physical blocking (zealots on the ramp, medics in front of marines, etc.), whether with a formation order or with a “hold position here” order.

In general, the tactics boss should be responsible for coordination of forces: These units in front, these units behind. Or: This squad holds, the squad comes in back for the sandwich. Or: This squad pokes at the front lines to draw defenders forward, then the drop goes in. The tactics boss should only issue orders with a reasonable chance of success. The unit boss is responsible for executing the orders so they have the best local chances of success, but doesn’t take responsibility for any coordination above the squad level; if the holding squad can’t hold or the sandwich squad gets intercepted, that’s the fault of the tactics boss.

The unit boss has to account for a lot of details in a short time, so it should work by a reactive algorithm. It can’t be as simple as the potential fields and flocking examples I wrote up, but something in the same vein. It can rely on data prepared by the tactics boss. In the spirit of reacting to the future, every friendly unit could have an intended course for the immediate future and every visible enemy unit a projected course, so that units coordinate more smoothly.

The tactics boss has to make complex decisions and, it seems to me, should work by search. (I think a knowledge-based tactics boss is possible, but I don’t see how to create one without first creating a search-based tactics boss.) I think the key decisions in starting to design the search are: 1. Can it be a flat search like MaasCraft’s, or does it want another level of abstraction, making it a hierarchical search? 2. How should it account for uncertainty?

A flat search says: I have these actions, the enemy has those actions, let’s look at sequences of actions for both sides and try to find good ones. A hierarchical search might say: I have these goals. For each goal, what actions (allowing for enemy reactions) might help me achieve it? Is the goal feasible? What combinations of goals can be satisfied? It’s hierarchical because it introduces a new level of abstraction (goals) and searches across both goals and actions. The hierarchical search is more complicated and may seem to do more work, but knowing your goals may also speed up the action search because you can prune away actions that don’t make sense. I don’t know which way is better in this case. It likely depends on implementation choices and stuff.

MaasCraft, as I understand it (I hope somebody will correct me if I make a mistake), doesn’t truly account for uncertainty. Its search allows for the enemy to maneuver squads that MaasCraft knows about; MaasCraft does nothing to protect itself against unscouted squads. In estimating how hard it will be to destroy a base, MaasCraft accounts for enemy reinforcements that may be produced during the attack, and that’s the extent of it.

It may be good enough for all I know, at least if scouting is thorough. But I suspect it would be better to consider the uncertainty about the enemy’s army: Number, locations, unit mix, what upgrades will finish this side of the search horizon. You don’t want to miss the point at which the first few high templar have storm energy. A few more ultralisks can turn a battle, and the enemy may have a few more than you expect, or be about to produce them.

The natural kind of search to use is something in the MCTS family, as MaasCraft does. Algorithms in this family work by drawing a statistical sample of action sequences for both sides and letting the stats talk out which sequences are better. Given that, a natural way to account for uncertainty is to draw a sample of enemy army details too: We don’t know if zerg concentrated on drones or hydras; if hydras, they may have gone here or over there.

I think a smart evaluation function will be a key ingredient. I find MaasCraft’s evaluation much too primitive and low-level. A smart evaluator, even if it’s slower than you think you can get away with, will make search much more effective. I know by experience. A smart evaluator can reduce the number of nodes a search needs to examine by orders of magnitude.

And that’s about as far as I can work it out without starting to make arbitrary implementation decisions. Have fun with it if you can!

Anyway, this is what makes sense to me. You should do what makes sense to you. But whatever else this is, it’s rather a lot for something I started out calling pathfinding!
