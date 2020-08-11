import React from 'react';
import './App.css';
import mapboxgl, { GeoJSONSourceRaw } from 'mapbox-gl'
import Settings from './settings'
import Select, { ValueType } from 'react-select'
import axios from "axios";
import { getColorCode } from "./colors"

type OptionType = {
  value: string;
  label: string;
};

interface AppState {
  selectOptions: OptionType[],
  map: mapboxgl.Map | undefined,
  lng: number | undefined
  lat: number | undefined
  zoom: number | undefined

}

class App extends React.Component<any, AppState> {



  constructor(props: any) {
    super(props);

    this.state = {
      selectOptions: [],
      map: undefined,
      lng: undefined,
      lat: undefined,
      zoom: undefined,
    }

  }

  componentDidMount() {

    let map = new mapboxgl.Map({
      container: "map",
      style: `https://api.maptiler.com/maps/${Settings.clientId}/style.json?key=${Settings.mapTilerKey}`
    });

    map.on('move', () => {
      this.setState({
        lng: map.getCenter().lng,
        lat: map.getCenter().lat,
        zoom: map.getZoom()
      });
    });

    this.setState({
      map
    })

    axios
      .get(`http://localhost:8080/collections/`)
      .then((response) => {
        if (response.status >= 200 && response.status < 300) {

          var newSelectOptions: OptionType[] = [];

          response.data.collections.forEach((e: any) => {
            newSelectOptions.push({ label: e.title, value: e.id })
          })

          this.setState({
            selectOptions: newSelectOptions
          })
        } else {
          console.error({ status: response.status, statusText: response.statusText });
        }
      })
      .catch((error: Error) => {
        console.error({ status: 400, statusText: error.message });
      });

  }

  handleChange = (selectedOptions: ValueType<OptionType>): void => {

    if (selectedOptions) {
      let options = selectedOptions as OptionType[];

      options.forEach((option: OptionType) => {
        axios.get(`http://localhost:8080/collections/${option.value}/items.json`).then((response) => {
          if (response.status >= 200 && response.status < 300) {
            console.log(response.data)

            if (this.state.map) {
              if (!this.state.map.getSource(option.value)) {
                const geoJsonSource: GeoJSONSourceRaw = {
                  "type": "geojson",
                  "data": response.data
                };
                this.state.map.addSource(option.value, geoJsonSource)

                this.state.map.addLayer({
                  'id': `${option.value}-Polygons`,
                  'type': 'fill',
                  'source': option.value,
                  'paint': {
                    'fill-color': getColorCode(option.value),
                    'fill-opacity': 0.4
                  },
                });

                this.state.map.addLayer({
                  'id': `${option.value}-Points`,
                  'type': 'circle',
                  'source': option.value,
                  'paint': {
                    'circle-radius': 6,
                    'circle-color': getColorCode(option.value)
                  },
                });

                console.log("Added everything")
              } else {
                //Make invisible Srces visible
              }
            } else {
              console.error("Map is not initialized yet")
            }
          } else {
            console.error({ status: response.status, statusText: response.statusText });
          }
        })
      })
    }
  }

  render() {
    return (
      <div>
        <div className="overlay">
          <Select
            options={this.state.selectOptions}
            isMulti
            onChange={option => this.handleChange(option)}
            placeholder="Select Collections"
          />
        </div>
        <div id="map" />
      </div>
    );
  }

}

export default App;
