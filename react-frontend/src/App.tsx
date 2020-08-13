import React from 'react';
import './App.css';
import './mapbox.css';
import mapboxgl, { GeoJSONSourceRaw } from 'mapbox-gl';
import Settings from './settings';
import Select, { ValueType } from 'react-select';
import axios from "axios";
import { getColorCode, colourStyles } from "./colors";
import { isNullOrUndefined } from 'util';



type OptionType = {
  value: string;
  label: string;
};

interface AppState {
  optionsToSelect: OptionType[],
  map: mapboxgl.Map | undefined,
  lng: number | undefined,
  lat: number | undefined,
  zoom: number | undefined,
  lastSelection: OptionType[]
}

class App extends React.Component<any, AppState> {

  constructor(props: any) {
    super(props);

    this.state = {
      optionsToSelect: [],
      map: undefined,
      lng: undefined,
      lat: undefined,
      zoom: undefined,
      lastSelection: []
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
            optionsToSelect: newSelectOptions
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
        if (this.state.map) {
          if (!this.state.map.getSource(option.value)) {
            axios.get(`http://localhost:8080/collections/${option.value}/items.json`).then((response) => {
              if (response.status >= 200 && response.status < 300) {
                //Unfortunately because this is a callback function to axios i have to make this check again
                const geoJsonSource: GeoJSONSourceRaw = {
                  "type": "geojson",
                  "data": response.data
                };
                this.state.map?.addSource(option.value, geoJsonSource)

                this.state.map?.addLayer({
                  'id': `${option.value}-Polygons`,
                  'type': 'fill',
                  'source': option.value,
                  'paint': {
                    'fill-color': getColorCode(option.value),
                    'fill-opacity': 0.4
                  },
                });

                this.state.map?.addLayer({
                  'id': `${option.value}-Points`,
                  'type': 'circle',
                  'source': option.value,
                  'paint': {
                    'circle-radius': 6,
                    'circle-color': getColorCode(option.value)
                  },
                });

                this.state.map?.on('click', `${option.value}-Points`, (e: any) => {
                  var coordinates = e.features[0].geometry.coordinates.slice();
                  var description = e.features[0].properties.description;
                  console.log(description)
                  console.log(coordinates)


                  // Ensure that if the map is zoomed out such that multiple
                  // copies of the feature are visible, the popup appears
                  // over the copy being pointed to.
                  while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
                    coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
                  }

                  if(this.state.map){
                  new mapboxgl.Popup()
                    .setLngLat(coordinates)
                    .setHTML(`<div>${description}</div>`)
                    .addTo(this.state.map);
                  } else {
                    console.error("Popup couldn't be shown because the map dissapeared")
                  }
                });

                // Change the cursor to a pointer when the mouse is over the places layer.
                this.state.map?.on('mouseenter', `${option.value}-Points`, () => {
                  (this.state.map as mapboxgl.Map).getCanvas().style.cursor = 'pointer';
                });

                // Change it back to a pointer when it leaves.
                this.state.map?.on('mouseleave', `${option.value}-Points`, () => {
                  (this.state.map as mapboxgl.Map).getCanvas().style.cursor = '';
                });



              } else {
                console.error({ status: response.status, statusText: response.statusText });
              }
            })
          } else {
            this.showCollection(option.value);
          }
        } else {
          console.error("Map is not initialized yet")
        }
      })

      this.state.lastSelection.forEach((e: OptionType) => {
        if (!options.includes(e)) {
          this.hideCollection(e.value)
        }
      })

      this.setState({
        lastSelection: options
      })
    } else {
      this.state.lastSelection.forEach((e: OptionType) => {
        this.hideCollection(e.value)
      })
    }
  }

  private hideCollection(collectionId: string) {
    if (this.state.map) {
      if (this.doesLayerExist(`${collectionId}-Polygons`)) {
        this.state.map.setLayoutProperty(`${collectionId}-Polygons`, 'visibility', 'none');
      }
      if (this.doesLayerExist(`${collectionId}-Points`)) {
        this.state.map.setLayoutProperty(`${collectionId}-Points`, 'visibility', 'none');
      }
    }
  }

  private showCollection(collectionId: string) {
    if (this.state.map) {
      if (this.doesLayerExist(`${collectionId}-Polygons`)) {
        this.state.map.setLayoutProperty(`${collectionId}-Polygons`, 'visibility', 'visible');
      }
      if (this.doesLayerExist(`${collectionId}-Points`)) {
        this.state.map.setLayoutProperty(`${collectionId}-Points`, 'visibility', 'visible');
      }
    }
  }

  private doesLayerExist = (layerId: string): boolean => {
    return !isNullOrUndefined(this.state.map?.getLayer(layerId))
  }

  render() {
    return (
      <div>
        <div className="overlay">
          <Select
            options={this.state.optionsToSelect}
            isMulti
            onChange={option => this.handleChange(option)}
            placeholder="Select Collections"
            styles={colourStyles}
          />
        </div>
        <div id="map" />
      </div>
    );
  }

}

export default App;
