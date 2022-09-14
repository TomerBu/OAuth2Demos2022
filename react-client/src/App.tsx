import {useEffect} from 'react'
import './App.css'

function App() {
  useEffect(() => {
    const url =
      'http://127.0.0.1:8000/oauth2/authorize?response_type=code&client_id=client&redirect_uri=http://127.0.0.1:8080/authorized&scope=openid'

      //window.location.href = url;
    // try {
    //   fetch(url)
    //   .then(res=>res.json())
    //   .then(json => {
    //     console.log(json)
    //   })
    // } catch (e) {
    //   console.log(e)
    // }
  }, [])
  return <div className="App">Hello world</div>
}

export default App
