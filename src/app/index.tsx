import React, { Children } from 'react';
import ReactDOM from 'react-dom';

import MainWindow from './components/MainWindow'
import {
  BrowserRouter,
  Routes,
  Route,
  Link,
} from "react-router-dom";
import MyWindow from './components/MyWindow';

// ReactDOM.render(<>  <Togglable thisStyle={{ 'type': 'button', 'value': 'Select Mode' }} children={<Table />} /></>
//   , document.getElementById('root'));
;
ReactDOM.render(<BrowserRouter><Routes>
  <Route path="/myfield/:value" element={<MyWindow/>} />
   <Route path="/" element={<MainWindow path={"myfield/order"}/>} />
</Routes>
 </BrowserRouter>
   , document.getElementById('root'));
// ReactDOM.render(<ContextTbl/>, document.getElementById('root'));

if (module.hot) {
  module.hot.accept();
}