package ke.eston.repoviewer.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ke.eston.repoviewer.R
import ke.eston.repoviewer.domain.model.Repository

@Composable
fun RepositoryListItem(repository: Repository) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(8.dp)
        ) {
            AsyncImage(
                model = repository.ownerAvatarUrl,
                contentDescription = repository.ownerHandle,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = repository.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(240.dp)
                )
                Text(
                    text = repository.ownerHandle,
                    modifier = Modifier.align(Alignment.TopEnd),
                    fontStyle = FontStyle.Italic
                )
                repository.language?.let {
                    Text(
                        text = stringResource(R.string.label_language, it),
                        modifier = Modifier.align(Alignment.BottomStart),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}