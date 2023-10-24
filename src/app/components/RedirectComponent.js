import { useNavigate } from "react-router-dom";
import React,{useState} from "react";
import { useParams } from 'react-router';
import "../../../node_modules/@syncfusion/ej2-icons/styles/material.css";

//children would be wrapped inside the RedirectComponent
export default function RedirectComponent({key, myIcon, path, children}) {
  const { value } = useParams();
  const navigate = useNavigate();
  
  const handleGoBack = () => {
    navigate("myfield/23-0002"); // new line
  };

  return React.createElement("div",{ style:{ display: "flex" }}, <button  onClick={handleGoBack} key={"RC"+ key} style={{display:"flex",justifyContent:"center",alignItems:"center"}} className={myIcon}></button>,children )
}
 