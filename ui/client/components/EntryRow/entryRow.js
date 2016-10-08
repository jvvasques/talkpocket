import React, { PropTypes } from 'react'
import classNames from 'classnames'

const EntryRow = ({articleUrl, soundUrl, state = 0}) => {
  
  const soundUrlText = state === 0 
    ? 'Translating text...'
    : soundUrl

  const stateIcon = state === 0 
    ? <div className="ui active inline loader"></div>
    : <i className="checkmark big icon"></i>;

  const iconClasses = classNames('one', 'wide', 'center', 'aligned', { positive: state !== 0 });

  const formattedArticleUrl = articleUrl.startsWith('http://') 
    ? articleUrl
    : 'http://' + articleUrl

  return (
        <tr>
            <td className="six wide">
                {formattedArticleUrl}
            </td>
            <td className="five wide ">
                {soundUrlText}
            </td>
            <td className={iconClasses}>
                {stateIcon}            
            </td>
        </tr>
    )
}

EntryRow.propTypes = {
    articleUrl: PropTypes.string.isRequired,
    soundUrl: PropTypes.string,
    image: PropTypes.string
}

export default EntryRow
