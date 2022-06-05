import { Component, React } from "react"
const Clothes = function(props) {

    console.log(props)
    return (
        <div>
            <h1>{props}</h1>
            <ul>
                <li><b>Name: </b>{props.name}</li>
                <li><b>Age: </b>{props.age}</li>
                <li><b>Address: </b>{props.address}</li>
            </ul>
        </div>

    )
}


export default Clothes