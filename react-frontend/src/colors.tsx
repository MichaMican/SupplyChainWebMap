import chroma from 'chroma-js'

interface HashTable<T> {
    [key: string]: T;
}

export const getColorCode = (id: string): string => {
    
    switch(id){
        case "gold":
            return "#FFC300" //yellow
        case "indium":
            return "#ABB2B9" //grey
        case "lithium":
            return "#2ECC71" //green
        case "zinn":
            return "#E74C3C" //red
        default:
            return "#FDFEFE" //white
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