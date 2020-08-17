import chroma from 'chroma-js'

interface HashTable<T> {
    [key: string]: T;
}


export const getColorCode = (id: string): string => {
    
    switch(id){
        case "PhoneAssembly":
            return "#C0392B"
        case "Semiconductors":
            return "#E74C3C"
        case "Electromechanical":
            return "#9B59B6"
        case "Connectors":
            return "#8E44AD"
        case "Display":
            return "#2980B9"
        case "Accessories":
            return "#3498DB"
        case "Passives":
            return "#1ABC9C"
        case "Cameras":
            return "#16A085"
        case "PrintedCircuitBoard":
            return "#27AE60"
        case "FlexiblePrintedCircuits":
            return "#2ECC71"
        case "SolderingPaste":
            return "#F1C40F"
        case "Battery":
            return "#F39C12"
        case "Plastics":
            return "#E67E22"
        case "Shields":
            return "#D35400"
        case "Packaging":
            return "#641E16"
        case "Gold":
            return "#7D6608"
        case "Tin":
            return "#4D5656"
        case "Tungsten":
            return "#4D5656"
        case "Tantalum":
            return "#154360"
        default:
            return "#000000" //black
    }
}

//styling of react select collections
export const colourStyles: any = {
    control: (styles: any) => ({ ...styles, backgroundColor: 'white' }),
    option: (styles: any, { data, isDisabled, isFocused, isSelected }: any) => {
      const color = chroma(getColorCode(data.value));
      return {
        ...styles,
        backgroundColor: isDisabled
          ? null
          : isSelected
          ? getColorCode(data.value)
          : isFocused
          ? color.alpha(0.1).css()
          : null,
        color: isDisabled
          ? '#ccc'
          : isSelected
          ? chroma.contrast(color, 'white') > 2
            ? 'white'
            : 'black'
          : getColorCode(data.value),
        cursor: isDisabled ? 'not-allowed' : 'default',
  
        ':active': {
          ...styles[':active'],
          backgroundColor: !isDisabled && (isSelected ? getColorCode(data.value) : color.alpha(0.3).css()),
        },
      };
    },
    multiValue: (styles: any, { data }: any) => {
      const color = chroma(getColorCode(data.value));
      return {
        ...styles,
        backgroundColor: color.alpha(0.1).css(),
      };
    },
    multiValueLabel: (styles: any, { data }: any) => ({
      ...styles,
      color: getColorCode(data.value),
    }),
    multiValueRemove: (styles: any, { data }: any) => ({
      ...styles,
      color: getColorCode(data.value),
      ':hover': {
        backgroundColor: getColorCode(data.value),
        color: 'white',
      },
    }),
  };