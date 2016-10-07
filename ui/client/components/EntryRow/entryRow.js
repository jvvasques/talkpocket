import React, { PropTypes } from 'react'

const EntryRow = ({articleUrl, soundUrl, image}) => {
  
  return (
        <tr>
            <td className="collapsing">
                {articleUrl}
            </td>
            <td>Sound: {soundUrl}</td>  
        </tr>
    )
}

EntryRow.propTypes = {
    articleUrl: PropTypes.string.isRequired,
    soundUrl: PropTypes.string,
    image: PropTypes.string
}

export default EntryRow
