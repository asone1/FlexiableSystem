import React, { useState } from "react";
import DefaultElement from "./DefaultElement";
import MyInput from "./MyInput";
// import { Redirect } from "react-router-dom";
import RedirectComponent from "./RedirectComponent";
// import {useContext}from 'react';
// import { CurrentTable } from './Table';
import "../../../node_modules/@syncfusion/ej2-icons/styles/material.css";

export default function Row(fields, colHead) {

   

    return <tr key={fields["id"]} id={fields["id"]}>
        {Object.keys(colHead).map((key, index) =>
            <td key={key}>{index === 0 ? <RedirectComponent path="/window" myIcon="e-icons e-edit" children={DefaultElement({ 'type': MyInput, 'value': fields[key] })} />
                : DefaultElement({ 'id': key, 'type': MyInput, 'value': fields[key] })}</td>
        )} </tr>;
}