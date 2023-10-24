import React, { useContext, useEffect, useState, createElement } from 'react';
import "./css/window.scss"
import { getClass, getFields, getObject,getObjectFromDB } from '../DataFromDB';
import { isBase64 } from './Utilities';
import { DataGroup } from './MainWindow';

export default function MyWindow() {
  // const [html, setHtml] = useState([]);
  // const profile = { data };
  const [profile, setProfile] = useState([]);
  const [headerData, setHeaderData] = useState([]);
  const { data, setData } = useContext(DataGroup);

  React.useEffect(() => {
    async function getColumns() {
        const d = await getObjectFromDB("fields");         
        
    }
    getColumns();
}, []);


  return <div className='container'>
    {
      Object.entries(profile).map(([id, value], index) => {
        return <><div key={"label" + id} className='label'><label htmlFor={id}>{headerData[id]}</label></div>
          <div key={id}> {isBase64(value) ? <img src={`data:image/jpeg;base64,${value}`} /> : <div key={"value" + id} className='column'><input type="text" id={id} value={value} /></div>} </div>    </>
      })}
  </div>
};