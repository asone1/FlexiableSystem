import React, { useContext, useEffect, useState, createElement } from 'react';
import "./css/window.scss"
import { getClass, getFields, getObject,getObjectFromDB } from '../DataFromDB';
import { isBase64 } from './Utilities';
import { DataGroup } from './MainWindow';

export default function FieldWindow() {
  // const [html, setHtml] = useState([]);
  // const profile = { data };
  const [profile, setProfile] = useState([]);
  const [headerData, setHeaderData] = useState([]);
  const { data, setData } = useContext(DataGroup);

  React.useEffect(()=>{
    if(Array.isArray(data)){
      let myProfile ={};
      let myHeader = {};
      data.forEach((field=>{
        var fieldId = field["id"];
        Object.keys(field).forEach(function(key){
            if(key!=="id"){
            myProfile[fieldId]= field[key];
            myHeader[fieldId]= key;}
        })
      }))
    }
    setProfile(myProfile);
    setHeaderData(myHeader);
  },[data]);

  return <div className='container'>
    {
      Object.entries(profile).map(([id, value], index) => {
        return <><div key={"label" + id} className='label'><label htmlFor={id}>{headerData[id]}</label></div>
          <div key={id}> {isBase64(value) ? <img src={`data:image/jpeg;base64,${value}`} /> : <div key={"value" + id} className='column'><input type="text" id={id} value={value} /></div>} </div>    </>
      })}
  </div>
};