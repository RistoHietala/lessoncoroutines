package fi.centria.tki.lessoncoroutines

class Route {
    var id: String = ""
    var name: String = ""
    var day: String = ""
    var time: String = ""
    var points: ArrayList<Point> = ArrayList()

    constructor(id: String, name: String, day: String, time: String, points: ArrayList<Point>) {
        this.id = id
        this.name = name
        this.day = day
        this.time = time
        this.points = points
    }


}