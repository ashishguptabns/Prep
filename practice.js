/* 
Lift Management System
 
Functional Requirement

Button
isPressed

FloorButton

UpButton

DownButton

OpenButton

CloseButton

Building
1. lifts
2. numFloors
3. findMeALift(direction, currFloor)

Lift
1. direction
2. startMoving()
3. stopMoving()
4. currFloor
5. destFloor
5. IsActive

Non Functional Requirement
1. Find fastest lift
2. Cater to multiple buildings


Implementation of a Use Case as discussed
 
Design - Classes and Interfaces
 
TestCases
 */

// SOLID

class Building {
    constructor(numFloors) {
        this.lifts = []
        this.numFloors = numFloors
    }
    addLift = (lift) => {
        this.lifts.push(lift)
    }
    findMeALift = (currFloor) => {
        let minDist = Infinity
        let bestLift
        for (const lift of this.lifts) {
            //  lift is not moving
            if (lift.currFloor === lift.destFloor) {
                if (Math.abs(currFloor - lift.destFloor) < minDist) {
                    bestLift = lift
                    minDist = Math.abs(currFloor - lift.destFloor)
                }
            } else {
                // find the distance it will cover before coming to curr floor and find the best lift
            }
        }

        return bestLift
    }
}

class Button {
    constructor() {
        this.isPressed = false
    }
}

class FloorButton extends Button {
    constructor(floorNum) {
        super()
        this.floorNum = floorNum
    }
}

const Actions = { up: 'moveUp', down: 'moveDown' }
class ActionButton extends Button {
    constructor(action) {
        super()
        this.action = action
    }
}

class UpButton extends ActionButton {
    constructor() {
        super(Actions.up)
    }
}

class DownButton extends Button {
    constructor() {
        super(Actions.down)
    }
}

class Lift {
    constructor(isActive) {
        this.isActive = isActive
        this.currFloor = 0
        this.destFloor = 0
        this.dir
    }

    setCurrFloor = (floor) => {
        this.currFloor = floor
    }
    setDestFloor = (floor) => {
        this.destFloor = floor
    }
    setDir = (dir) => {
        this.dir = dir
    }
}